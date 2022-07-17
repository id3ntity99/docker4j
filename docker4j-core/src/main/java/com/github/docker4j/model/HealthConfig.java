package docker4j.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A config for testing that checks the container is healthy; the container is working normally.
 */
public class HealthConfig {
    @JsonProperty("Test")
    private String[] test; // Possible values: [], ["NONE"], ["CMD", args...], ["CMD-SHELL", command]
    @JsonProperty("Interval")
    private int interval; // Nanosecond
    @JsonProperty("Timeout")
    private int timeout;
    @JsonProperty("Retries")
    private int retries;
    @JsonProperty("StartPeriod")
    private int startPeriod;

    public String[] getTest() {
        return test;
    }

    /**
     * A test to perform to check a container's health;
     *
     * @param test A test to perform. Possible values are: [], ["NONE"], ["CMD", args...], ["CMD-SHELL", command]
     * @return <code>HealthConfig</code> object with assigned <code>test</code> field.
     */
    public HealthConfig setTest(String[] test) {
        this.test = test;
        return this;
    }

    public int getInterval() {
        return interval;
    }

    /**
     * An interval between health checks.
     *
     * @param interval Nanoseconds. This parameter should be 0 or at least 1000000 (1 ms). 0 means inherit.
     * @return <code>HealthConfig </code> object with assigned <code>interval</code> field.
     */
    public HealthConfig setInterval(int interval) throws IllegalArgumentException {
        if (interval >= 1000000 || interval == 0) {
            this.interval = interval;
        } else {
            String errMsg = String.format(
                    "interval should be 0 or at least 1000000 nanoseconds (= 1 ms), received: %d", interval
            );
            throw new IllegalArgumentException(errMsg);
        }
        return this;
    }

    public int getTimeout() {
        return timeout;
    }

    /**
     * A timeout to check if the test is hanging.
     * @param timeout Nanoseconds. This parameter should be 0 or at least 1000000 (1 ms). 0 means inherit.
     * @return <code>HealthConfig </code> object with assigned <code>timeout </code> field.
     */
    public HealthConfig setTimeout(int timeout) {
        if (timeout >= 1000000 || timeout == 0) {
            this.timeout = timeout;
        } else {
            String errMsg = String.format(
                    "timeout should be 0 or at least 1000000 nanoseconds (= 1 ms), received: %d", timeout
            );
            throw new IllegalArgumentException(errMsg);
        }
        return this;
    }

    public int getRetries() {
        return retries;
    }

    /**
     * A number of successive retries to consider a container as unhealthy. 0 means inherit.
     * @param retries A number of successive retries.
     * @return HealthConfig object with assigned retries field.
     */
    public HealthConfig setRetries(int retries) {
        this.retries = retries;
        return this;
    }

    public int getStartPeriod() {
        return startPeriod;
    }

    /**
     * start-period determines when to start the health-check.
     * For example, if the <code>startPeriod</code> field is set to 30000000000, then the health-check starts from 30 seconds after booting up the container. 0 means inherit.
     * @param startPeriod Nanoseconds. The start time of health-check.
     * @return <code>HealthConfig </code> object with assigned <code>startPeriod</code> field.
     */
    public HealthConfig setStartPeriod(int startPeriod) {
        this.startPeriod = startPeriod;
        return this;
    }
}
