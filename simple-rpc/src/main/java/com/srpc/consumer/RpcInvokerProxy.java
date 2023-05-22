package com.srpc.consumer;

import com.srpc.common.RpcRequest;
import com.srpc.common.RpcRequestHolder;
import com.srpc.protocol.MsgHeader;
import com.srpc.protocol.MsgType;
import com.srpc.protocol.ProtocolConstants;
import com.srpc.protocol.RpcProtocol;
import com.srpc.protocol.seriallzation.SerializationTypeEnum;
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
        RpcProtocol<RpcRequest> protocol = new RpcProtocol<>();
        //build header
        MsgHeader header = new MsgHeader.Builder()
                .setRequestId(RpcRequestHolder.REQUEST_ID_GEN.incrementAndGet())
                .setMagic(ProtocolConstants.MAGIC)
                .setVersion(ProtocolConstants.VERSION)
                .setSerialization((byte) SerializationTypeEnum.HESSIAN.getType())
                .setMsgType((byte)MsgType.REQUEST.ordinal())
                .setStatus((byte) 0x1)
                .build();
        protocol.setHeader(header);
        //build request body
        RpcRequest request = new RpcRequest();
        request.setServiceVersion(this.serviceVersion);
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParams(args);
        protocol.setBody(request);

        return null;
    }
}
