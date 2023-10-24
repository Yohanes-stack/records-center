package test.srpc.consumer;

import test.srpc.common.*;
import test.srpc.protocol.MsgHeader;
import test.srpc.protocol.MsgType;
import test.srpc.protocol.ProtocolConstants;
import test.srpc.protocol.RpcProtocol;
import test.srpc.protocol.seriallzation.SerializationTypeEnum;
import test.srpc.registry.RegistryFactory;
import test.srpc.registry.RegistryService;
import test.srpc.registry.RegistryType;
import test.srpc.registry.loadbalancer.LoadBalancerType;
import test.srpc.tolerant.FaultTolerantType;
import io.netty.channel.DefaultEventLoop;
import io.netty.util.concurrent.DefaultPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RpcInvokerProxy implements InvocationHandler {

    private Logger logger = LoggerFactory.getLogger(RpcInvokerProxy.class);

    private String serviceVersion;

    private long timeout;

    private LoadBalancerType loadBalancerType;

    private RegistryService registryService;

    private FaultTolerantType faultTolerantType;

    private long retryCount;

    public RpcInvokerProxy(String serviceVersion, long timeout, String faultTolerantType, String loadBalancerType, String registryType, long retryCount) throws Exception {
        this.serviceVersion = serviceVersion;
        this.timeout = timeout;
        this.loadBalancerType = LoadBalancerType.toLoadBalancer(loadBalancerType);
        this.faultTolerantType = FaultTolerantType.toFaultTolerant(faultTolerantType);
        this.registryService = RegistryFactory.getInstance(RegistryType.valueOf(registryType));
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
                .setMsgType((byte) MsgType.REQUEST.ordinal())
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

        RpcConsumer rpcConsumer = new RpcConsumer();
        RpcFuture<RpcResponse> future = new RpcFuture<>(new DefaultPromise<>(new DefaultEventLoop()), timeout);
        RpcRequestHolder.REQUEST_MAP.put(header.getRequestId(), future);
        String serviceKey = RpcServiceNameBuilder.buildServiceKey(request.getClassName(), request.getServiceVersion());
        Object[] params = request.getParams();
        //计算哈希
        int invokerHashCode = serviceKey.hashCode();
        ServiceMeta serviceMeta = registryService.discovery(serviceKey, invokerHashCode, loadBalancerType);
        //故障转移
        List<ServiceMeta> serviceMetaList = this.registryService.discoveries(serviceKey);

        long count = 1;
        while (count <= retryCount) {
            try {
                rpcConsumer.sendRequest(protocol, serviceMeta);
                RpcResponse rpcResponse = future.getPromise().get(future.getTimeout(), TimeUnit.MILLISECONDS);
                logger.warn("rpc call successful, serviceKey : {}", serviceKey);
                return rpcResponse.getData();
            } catch (Exception e) {
                switch (faultTolerantType) {
                    //快速失败
                    case FailFast:
                        logger.warn("rpc call failed,triggering FailFast strategy");
                        break;
                    //故障转移
                    case Failover:
                        logger.warn("rpc调用失败，第{}次重试", count);
                        count++;
                        serviceMetaList.remove(serviceMeta);
                        if (!ObjectUtils.isEmpty(serviceMetaList)) {
                            serviceMeta = serviceMetaList.get(0);
                        } else {
                            logger.warn("rpc 调用失败,无服务可用 serviceKey: {}", serviceKey);
                            count = retryCount;
                        }
                        //忽略这次请求错误
                    case Failsafe:
                        return null;
                }
            }

        }

        throw new RuntimeException("RPC调用失败，超过最大重试次数：" + retryCount);
    }
}
