package com.github.docker4j;

import com.github.docker4j.exceptions.DuplicationException;
import io.netty.buffer.ByteBufAllocator;
import io.netty.util.concurrent.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @deprecated {@link DockerClient} implements the features of this class.
 * This class links {@link DockerHandler}s and has internal array of {@link DockerHandler}. <br/>
 * For example, let's say you added 3 {@link DockerHandler}s: {@link CreateContainerHandler},
 * {@link StartContainerHandler}, {@link ExecCreateHandler} respectively.<br/>
 * If the {@link #link()} is invoked, the linker checks if there are any duplications of {@link DockerHandler} instances,
 * which have same {@link #hashCode()}.<br/>
 * After the duplication check, the linking process begins which follows these steps: <br />
 * 1. Pick the first request, {@link CreateContainerHandler}.<br />
 * 2. Pick its next request, {@link StartContainerHandler}.<br />
 * 3. Set {@link CreateContainerHandler#nextRequest} to {@link StartContainerHandler}.<br />
 * 4. Set {@link CreateContainerHandler#allocator} to {@link RequestLinker#allocator}.<br />
 * Update {@link RequestLinker#handlers}.<br />
 * 5. Pick the second request, {@link StartContainerHandler}.<br />
 * 6. Pick its next request, {@link ExecCreateHandler}.<br />
 * 7. Set {@link StartContainerHandler#nextRequest} to {@link StartContainerHandler}.<br />
 * 8. Set {@link StartContainerHandler#allocator} to {@link RequestLinker#allocator}.<br />
 * Update {@link RequestLinker#handlers}.<br />
 * 9. Pick the final request, {@link ExecCreateHandler}.<br />
 * 10. Since {@link ExecCreateHandler} is the last request, next request doesn't exist.<br />
 * 11. Set {@link ExecCreateHandler#allocator} to {@link RequestLinker#allocator}.<br />
 * Update {@link RequestLinker#handlers}.<br />
 * <br/>
 * <strong>Infinite Request Problem</strong> <br />
 * Note that, you need to provide different instance of {@link DockerHandler} to this linker.
 * 이유: <br/>
 * 자, 여기에 A1, B1, A2, B2 4개의 DockerRequest가 있다. 이 때, A1와 A2는 같은 인스턴스, 즉 하나의 객체를 재사용했음을 의미한다. B1과 B2도 마찬가지이다.
 * 4개의 요청을 RequestLinker에 넣고 {@link #link()}를 호출하면 다음과 같은 일이 일어난다.<br/>
 * A1.setNext(B1)<br/>
 * B1.setNext(A2)<br/>
 * A2.setNext(B2)<br/>
 * 위에서 언급했듯이 A1과 A2는 같은 인스턴스이므로 <code>A1.nextRequest == B1</code>, <code>B1.nextRequest == A1</code>이 된다.
 * 이 상태에서 {@link DefaultDockerClient#request()}를 호출하면:<br/>
 * (1) A1의 FullHttpRequest가 전송되고 응답을 받은 A1의 핸들러는 다음 요청이 있는지를 살핀다.
 * A1의 다음 요청은 B1이므로 B1의 FullHttpRequest를 렌더링해 전송하고 B1의 핸들러를 생성한다.<br/>
 * (2) B1 요청에 대한 응답을 받은 B1 핸들러는 마찬가지로 다음 요청이 있는지 살핀다. 앞서 말했듯이, B1.nextRequest == A2 == A1이므로 다시 (1)번부터 반복되므로
 * <strong>Infinite request</strong>가 발생하는 것이다.
 * {@link #checkDuplicates()} 메서드는 이를 방지하기위해 존재한다.
 * 설명이 잘 되었는지는 모르겠으나, 이해가 안된다면 이것 하나만 기억하라: 절대로 {@link DockerHandler}의 인스턴스를 {@link RequestLinker}에 재사용하지 마라!
 */
@Deprecated
public class RequestLinker {
    private final Logger logger = LoggerFactory.getLogger(RequestLinker.class);
    private final List<DockerHandler> handlers;
    private DockerResponseNode responseNode = new DockerResponseNode();
    private ByteBufAllocator allocator;
    private Promise<DockerResponseNode> promise;

    public RequestLinker(int size) {
        handlers = new ArrayList<>();
    }

    public void withResponse(DockerResponseNode responseNode) {
        this.responseNode = responseNode;
    }

    public DockerHandler get(int index) {
        return handlers.get(index);
    }

    public RequestLinker add(DockerHandler request) {
        handlers.add(request);
        return this;
    }

    public void clear() {
        handlers.clear();
    }

    void setAllocator(ByteBufAllocator allocator) {
        this.allocator = allocator;
    }

    void setPromise(Promise<DockerResponseNode> promise) {
        this.promise = promise;
    }

    private void checkDuplicates() throws DuplicationException {
        DockerHandler requestToCheck;
        int hashToCheck;
        for (int i = 0; i < handlers.size(); i++) {
            requestToCheck = handlers.get(i);
            hashToCheck = requestToCheck.hashCode();
            for (int j = 0; j < handlers.size(); j++) {
                if (j == i) {
                    continue;
                }
                if (hashToCheck == handlers.get(j).hashCode()) {
                    String err = String.format("Duplication detected: %s %s", requestToCheck.getClass(), handlers.get(j));
                    String message = "You need to create new instance of DockerRequest, even for the same operation." +
                            "Do not reuse the instances!";
                    throw new DuplicationException(err + message);
                }
            }
        }
        logger.debug("There are no duplications");
    }

    RequestLinker link() throws DuplicationException {
        checkDuplicates();
        final int lastIndex = handlers.size() - 1;
        DockerHandler nextRequest;
        DockerHandler currentRequest;
        for (int i = 0; i <= handlers.size() - 1; i++) {
            currentRequest = handlers.get(i);
            if (i == 0) {
                currentRequest.setAllocator(allocator)
                        .setNode(this.responseNode)
                        .setPromise(promise);
            }
            if (i != lastIndex) {
                nextRequest = handlers.get(i + 1);
                currentRequest.setNext(nextRequest);
                logger.debug("Linked: ({}, {}) =====> ({}, {})", currentRequest, currentRequest.hashCode(), nextRequest, nextRequest.hashCode());
            }
            handlers.set(i, currentRequest);
        }
        logger.debug("Completed request linking {}", handlers);
        return this;
    }
}
