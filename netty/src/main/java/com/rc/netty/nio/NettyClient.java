package com.rc.netty.nio;

import com.rc.netty.protocol.ClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class NettyClient {

    private static int MAX_RETRY = 5;
    public static void main(String[] args) {
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .handler(new ClientHandler());
        connect(bootstrap,"127.0.0.1",8000,5);
    }
    private static void connect(Bootstrap bootstrap,String host,int port,int retry){
        bootstrap.connect(host,port).addListener( future -> {
           if(future.isSuccess()){
               System.out.println("连接成功");
           }else if(retry == 0){
               System.out.println("重试失败,放弃链接!");
           }else{
               // 第几次重连
               int order = (MAX_RETRY - retry) + 1;
               // 本次重连的间隔，1<<order相当于1乘以2的order次方
               int delay = 1 << order;
               System.err.println(new Date() + ": 连接失败，第" + order + "次重连……");
               bootstrap.config().group().schedule(() -> connect(bootstrap, host, port, retry - 1), delay, TimeUnit
                       .SECONDS);
           }
        });
    }
}
