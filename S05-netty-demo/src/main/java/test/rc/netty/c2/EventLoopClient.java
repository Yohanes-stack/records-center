package test.rc.netty.c2;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;

public class EventLoopClient {
    public static void main(String[] args) throws InterruptedException {
        //1.启动器
        ChannelFuture channelFuture = new Bootstrap()
                //2.添加EventLoop
                .group(new NioEventLoopGroup())
                //3.选择客户端的channel实现
                .channel(NioSocketChannel.class)
                //4.添加处理器
                .handler(new ChannelInitializer<>() {
                    //连接建立后被调用
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast(new StringEncoder());
                    }
                })
                //5.连接服务器
                .connect(new InetSocketAddress("localhost", 8888));
        //2.1无阻塞向下执行
//        Channel channel = channelFuture
//                //阻塞当前线程，直到nio线程连接建立完毕
//                .sync()
//                .channel();
//        System.out.println(channel);
//        System.out.println("");
//        channel.writeAndFlush("ccc");
        //2.2使用addListener方法异步处理结果
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            //在nio线程建立好之后，会调用operationComplete
            public void operationComplete(ChannelFuture future) throws Exception {
                Channel channel = future.channel();
                channel.writeAndFlush("ccc");
            }
        });
    }
}
