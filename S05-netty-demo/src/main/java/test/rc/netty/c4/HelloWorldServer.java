package test.rc.netty.c4;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.util.Date;

@Slf4j
public class HelloWorldServer {
    public static void main(String[] args) {
        start();
    }

    public static void start() {

        try {
            NioEventLoopGroup boss = new NioEventLoopGroup();
            NioEventLoopGroup worker = new NioEventLoopGroup();
            ServerBootstrap serverBootstrap =
                    new ServerBootstrap()
                            .channel(NioServerSocketChannel.class)
                            .group(boss, worker)
                            //调整系统的接受缓冲区（滑动窗口）
//                            .option(ChannelOption.SO_RCVBUF, 5)
                            //调整netty的接受缓冲区（byteBuf）
                            .childOption(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(16, 16, 16))
                            .childHandler(new ChannelInitializer<>() {
                                @Override
                                protected void initChannel(Channel ch) throws Exception {
                                    ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));

                                }
                            });
            ChannelFuture channelFuture = serverBootstrap.bind(9999).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
