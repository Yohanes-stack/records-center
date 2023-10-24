package test.srpc.consumer;

import test.srpc.common.RpcRequest;
import test.srpc.common.ServiceMeta;
import test.srpc.protocol.RpcProtocol;
import test.srpc.protocol.codec.RpcDecoder;
import test.srpc.protocol.codec.RpcEncoder;
import test.srpc.protocol.handler.RpcResponseHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcConsumer {
    private Logger logger = LoggerFactory.getLogger(RpcConsumer.class);

    private final Bootstrap bootstrap;

    private final EventLoopGroup eventLoopGroup;

    public RpcConsumer() {
        bootstrap = new Bootstrap();
        eventLoopGroup = new NioEventLoopGroup(4);
        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        channel.pipeline()
                                .addLast(new RpcEncoder())
                                .addLast(new RpcDecoder())
                                .addLast(new RpcResponseHandler());
                    }
                });
    }

    /**
     * send request
     * @param protocol
     * @param serviceMeta
     */
    public void sendRequest(RpcProtocol<RpcRequest> protocol, ServiceMeta serviceMeta) throws InterruptedException {
        if (serviceMeta == null) {
            return;
        }
        ChannelFuture future = bootstrap.connect(serviceMeta.getServiceAddr(), serviceMeta.getServicePort()).sync();
        future.addListener((ChannelFutureListener) args -> {
            if (future.isSuccess()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Successfully connected to the RPC service {}, port:{}"
                            , serviceMeta.getServiceAddr()
                            , serviceMeta.getServicePort());
                }
            } else {
                logger.error("Failed to connected to the RPC service {}, port:{}"
                        , serviceMeta.getServiceAddr()
                        , serviceMeta.getServicePort());
                future.cause().printStackTrace();
                eventLoopGroup.shutdownGracefully();
            }
        });
        future.channel().writeAndFlush(protocol);
    }
}
