package com.srpc.protocol.handler;

import com.srpc.common.RpcRequest;
import com.srpc.common.RpcResponse;
import com.srpc.common.RpcServiceNameBuilder;
import com.srpc.protocol.MsgHeader;
import com.srpc.protocol.MsgStatus;
import com.srpc.protocol.MsgType;
import com.srpc.protocol.RpcProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.cglib.reflect.FastClass;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class RpcRequestHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcRequest>> {

    // 缓存服务
    private final Map<String, Object> rpcServiceMap;

    public RpcRequestHandler(Map<String, Object> rpcServiceMap) {
        this.rpcServiceMap = rpcServiceMap;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcProtocol<RpcRequest> protocol) throws Exception {
        RpcRequestProcessor.submitRequest(() -> {
            RpcProtocol<RpcResponse> resProtocol = new RpcProtocol<>();
            RpcResponse response = new RpcResponse();
            MsgHeader header = protocol.getHeader();
            header.setMsgType((byte) MsgType.RESPONSE.ordinal());
            try {
                Object result = handler(protocol.getBody());
                response.setData(result);
                header.setStatus((byte) MsgStatus.SUCCESS.ordinal());
                resProtocol.setHeader(header);
                resProtocol.setBody(response);
            } catch (InvocationTargetException e) {
                header.setStatus((byte) MsgStatus.FAILED.ordinal());
                response.setMessage(e.toString());
            }
            ctx.writeAndFlush(resProtocol);
        });
    }

    private Object handler(RpcRequest request) throws InvocationTargetException {
        String serviceKey = RpcServiceNameBuilder.buildServiceKey(request.getClassName(), request.getServiceVersion());
        Object serviceBean = rpcServiceMap.get(serviceKey);

        if (serviceBean == null) {
            throw new RuntimeException("service not exist");
        }
        Class<?> serviceClass = serviceBean.getClass();
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] params = request.getParams();
        FastClass fastClass = FastClass.create(serviceClass);
        int index = fastClass.getIndex(methodName, parameterTypes);
        return fastClass.invoke(index, serviceBean, params);
    }
}
