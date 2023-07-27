package com.rc.netty.c4;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class HelloWorldClient {
    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            send();
        }
        System.out.println("finish");
    }

    public static void send() {
        NioEventLoopGroup worker = null;
        try {
            worker = new NioEventLoopGroup();
            Bootstrap bootstrap =
                    new Bootstrap()
                            .channel(NioSocketChannel.class)
                            .group(worker)
                            .handler(new ChannelInitializer<>() {
                                @Override
                                protected void initChannel(Channel ch) throws Exception {
                                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                        @Override
                                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                            //会在连接channel建立成功后，会触发active事件
                                            ByteBuf buf = ctx.alloc().buffer(2);
                                            buf.writeBytes(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 9, 10});
                                            ctx.writeAndFlush(buf);
                                            ctx.channel().close();
                                        }
                                    });
                                }
                            });
            bootstrap.connect("localhost", 9999);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            worker.shutdownGracefully();
        }
    }
}
