package com.srpc.tolerant;

/**
 * 容错类型
 */
public enum FaultTolerantType {

    Failover,

    FailFast,

    Failsafe;

    public static FaultTolerantType toFaultTolerant(String loadBalancer) {
        for (FaultTolerantType value : values()) {
            if (value.toString().equals(loadBalancer)) {
                return value;
            }
        }
        return null;
    }
}
