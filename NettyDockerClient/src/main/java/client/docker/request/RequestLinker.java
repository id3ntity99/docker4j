package client.docker.request;

import client.docker.dockerclient.NettyDockerClient;
import client.docker.request.exceptions.DuplicationException;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * This class links {@link DockerRequest}s and has internal array of {@link DockerRequest}. <br/>
 * For example, let's say you added 3 {@link DockerRequest}s: {@link CreateContainerRequest},
 * {@link StartContainerRequest}, {@link ExecCreateRequest} respectively.<br/>
 * If the {@link #link()} is invoked, the linker checks if there are any duplications of {@link DockerRequest} instances,
 * which have same {@link #hashCode()}.<br/>
 * After the duplication check, the linking process begins which follows these steps: <br />
 * 1. Pick the first request, {@link CreateContainerRequest}.<br />
 * 2. Pick its next request, {@link StartContainerRequest}.<br />
 * 3. Set {@link CreateContainerRequest#nextRequest} to {@link StartContainerRequest}.<br />
 * 4. Set {@link CreateContainerRequest#allocator} to {@link RequestLinker#allocator}.<br />
 * Update {@link RequestLinker#requests}.<br />
 * 5. Pick the second request, {@link StartContainerRequest}.<br />
 * 6. Pick its next request, {@link ExecCreateRequest}.<br />
 * 7. Set {@link StartContainerRequest#nextRequest} to {@link StartContainerRequest}.<br />
 * 8. Set {@link StartContainerRequest#allocator} to {@link RequestLinker#allocator}.<br />
 * Update {@link RequestLinker#requests}.<br />
 * 9. Pick the final request, {@link ExecCreateRequest}.<br />
 * 10. Since {@link ExecCreateRequest} is the last request, next request doesn't exist.<br />
 * 11. Set {@link ExecCreateRequest#allocator} to {@link RequestLinker#allocator}.<br />
 * Update {@link RequestLinker#requests}.<br />
 * <br/>
 * <strong>Infinite Request Problem</strong> <br />
 * Note that, you need to provide different instance of {@link DockerRequest} to this linker.
 * 이유: <br/>
 * 자, 여기에 A1, B1, A2, B2 4개의 DockerRequest가 있다. 이 때, A1와 A2는 같은 인스턴스, 즉 하나의 객체를 재사용했음을 의미한다. B1과 B2도 마찬가지이다.
 * 4개의 요청을 RequestLinker에 넣고 {@link #link()}를 호출하면 다음과 같은 일이 일어난다.<br/>
 * A1.setNext(B1)<br/>
 * B1.setNext(A2)<br/>
 * A2.setNext(B2)<br/>
 * 위에서 언급했듯이 A1과 A2는 같은 인스턴스이므로 <code>A1.nextRequest == B1</code>, <code>B1.nextRequest == A1</code>이 된다.
 * 이 상태에서 {@link NettyDockerClient#request()}를 호출하면:<br/>
 * (1) A1의 FullHttpRequest가 전송되고 응답을 받은 A1의 핸들러는 다음 요청이 있는지를 살핀다.
 * A1의 다음 요청은 B1이므로 B1의 FullHttpRequest를 렌더링해 전송하고 B1의 핸들러를 생성한다.<br/>
 * (2) B1 요청에 대한 응답을 받은 B1 핸들러는 마찬가지로 다음 요청이 있는지 살핀다. 앞서 말했듯이, B1.nextRequest == A2 == A1이므로 다시 (1)번부터 반복되므로
 * <strong>Infinite request</strong>가 발생하는 것이다.
 * {@link #checkDuplicates()} 메서드는 이를 방지하기위해 존재한다.
 * 설명이 잘 되었는지는 모르겠으나, 이해가 안된다면 이것 하나만 기억하라: 절대로 {@link DockerRequest}의 인스턴스를 {@link RequestLinker}에 재사용하지 마라!
 */
public class RequestLinker {
    private final Logger logger = LoggerFactory.getLogger(RequestLinker.class);
    /**
     * An array of {@link DockerRequest} that is managed internally.
     */
    private final DockerRequest[] requests;
    /**
     * An index of the last {@link DockerRequest}
     */
    private int tail = 0;
    /**
     * A {@link ByteBufAllocator} field, which is initialized by {@link RequestLinker#setAllocator(ByteBufAllocator)}
     * This allocator will be passed to the each {@link DockerRequest} and used by {@link DockerRequest#render()}.
     */
    private ByteBufAllocator allocator;

    /**
     * Create new RequestLinker with specified size.
     *
     * @param size A size of the internal array of {@link DockerRequest}.
     */
    public RequestLinker(int size) {
        requests = new DockerRequest[size];
    }

    /**
     * Set allocator.
     *
     * @param allocator {@link ByteBufAllocator}. Use {@link Channel#alloc()} to acquire.
     */
    public void setAllocator(ByteBufAllocator allocator) {
        this.allocator = allocator;
    }

    /**
     * Get a {@link DockerRequest} at the specified index.
     *
     * @param index
     * @return
     */
    public DockerRequest get(int index) {
        return requests[index];
    }

    /**
     * Get length of the {@link RequestLinker#requests};
     *
     * @return length of {@link RequestLinker#requests}.
     */
    public int length() {
        return tail;
    }

    /**
     * Add new {@link DockerRequest}. Mind the size specified when instantiating the linker.
     *
     * @param request {@link DockerRequest} to add.
     * @return
     */
    public RequestLinker add(DockerRequest request) {
        if (tail < requests.length) {
            this.requests[tail] = request;
            tail++;
        } else {
            throw new ArrayIndexOutOfBoundsException("You cannot add anymore. Exceeded defined size");
        }
        return this;
    }

    /**
     * Check if there are duplication. The "duplication" means same instances that have same hashCodes.
     *
     * @throws DuplicationException
     */
    private void checkDuplicates() throws DuplicationException {
        DockerRequest requestToCheck;
        int hashToCheck;
        for (int i = 0; i < tail; i++) {
            requestToCheck = requests[i];
            hashToCheck = requestToCheck.hashCode();
            for (int k = 0; k < tail; k++) {
                if (k == i) {
                    continue;
                }
                if (hashToCheck == requests[k].hashCode()) {
                    String err = String.format("Duplication detected: %s %s", requestToCheck.getClass(), requests[k]);
                    String message = "You need to create new instance of DockerRequest, even for the same operation." +
                            "Do not reuse the instances!";
                    throw new DuplicationException(err + message);
                }
            }
        }
        logger.debug("There are no duplications");
    }

    /**
     * Start the linking process.
     *
     * @return
     * @throws DuplicationException
     */
    public RequestLinker link() throws DuplicationException {
        checkDuplicates();
        final int lastIndex = tail - 1;
        DockerRequest nextRequest;
        DockerRequest currentRequest;
        for (int i = 0; i <= tail - 1; i++) {
            currentRequest = requests[i];  /*Get current index's request;*/
            if (i != lastIndex) { // If current request has next;
                nextRequest = requests[i + 1]; /* Get next index's request; */
                currentRequest.setNext(nextRequest); /* Set next request to current request; */
                String log = String.format("Linked: (%s, %d) =====> (%s, %d)", currentRequest, currentRequest.hashCode(), nextRequest, nextRequest.hashCode());
                logger.debug(log);
                currentRequest.setAllocator(allocator);
            }
            requests[i] = currentRequest; /* update request in the array*/
        }
        String log = String.format("Completed request linking: %s", Arrays.toString(requests));
        logger.debug(log);
        return this;
    }
}
