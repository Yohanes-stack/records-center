package test.rc.netty.c2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.nio.charset.Charset;

public class EventLoopServer {
    public static void main(String[] args) {
        //细分2：创建了一个独立的EventLoopGroup
        EventLoopGroup group = new DefaultEventLoopGroup();
        new ServerBootstrap()
                //boss 和worker
                //细分 boss 只负责accept事件 worker只负责 socketChannel上的读写
                .group(new NioEventLoopGroup(), new NioEventLoopGroup(2))
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast( "handler1", new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object o) throws Exception {
                                ByteBuf byteBuf = (ByteBuf) o;
                                System.out.println(byteBuf.toString(Charset.defaultCharset()));
                                //将消息传递给下一个handler
                                ctx.fireChannelRead(o);
                            }
                        }).addLast(group, "handler2", new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
                                ByteBuf byteBuf = (ByteBuf) o;
                                System.out.println(byteBuf.toString(Charset.defaultCharset()));
                            }
                        });
                    }
                }).bind(8888);
    }
}
