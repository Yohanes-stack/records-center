package com.srpc.registry.loadbalancer;

import java.util.HashMap;
import java.util.Map;

/**
 * 负载均衡工厂
 */
public class LoadBalancerFactory {

    private static Map<LoadBalancerType,ServiceLoadBalancer> serviceLoadBalancerMap = new HashMap<>();

    static{
        //注册负载均衡实例
    }
    public static ServiceLoadBalancer getInstance(LoadBalancerType type){
        return serviceLoadBalancerMap.get(type);
    }
}
