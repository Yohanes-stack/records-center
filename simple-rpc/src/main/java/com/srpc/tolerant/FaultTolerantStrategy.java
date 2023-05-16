package com.srpc.tolerant;

/**
 * 容错策略接口
 */
@FunctionalInterface
public interface FaultTolerantStrategy {

    void handler();
}
