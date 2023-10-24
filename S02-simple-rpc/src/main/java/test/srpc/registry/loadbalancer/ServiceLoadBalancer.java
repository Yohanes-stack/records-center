package test.srpc.registry.loadbalancer;

import test.srpc.common.ServiceMeta;

import java.util.List;

/**
 * 负载均衡
 * @param <T>
 */
public interface ServiceLoadBalancer <T>{

    ServiceMeta select(List<T> servers,int hashCode);
}
