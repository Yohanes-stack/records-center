package com.srpc.consumer;

import com.srpc.registry.RegistryService;
import com.srpc.registry.loadbalancer.LoadBalancerType;
import com.srpc.tolerant.FaultTolerantType;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class RpcInvokerProxy implements InvocationHandler {

    private String serviceVersion;

    private long timeout;

    private LoadBalancerType loadBalancerType;

    private RegistryService registryService;

    private FaultTolerantType faultTolerantType;

    private long retryCount;

    public RpcInvokerProxy(String serviceVersion, long timeout,String faultTolerantType,String loadBalancerType, String registryType,long retryCount) throws Exception {
        this.serviceVersion = serviceVersion;
        this.timeout = timeout;
        this.loadBalancerType = LoadBalancerType.toLoadBalancer(loadBalancerType);
        this.faultTolerantType = FaultTolerantType.toFaultTolerant(faultTolerantType);
//        this.registryService = RegistryFactory.getInstance(RegistryType.valueOf(registryType));
        this.retryCount = retryCount;

    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        return null;
    }
}
