package com.rc.netty.c1;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

public class HelloServer {
    public static void main(String[] args) {
        //1.启动器 负责组装netty组件，启动服务器
        new ServerBootstrap()
                //2. BossEventLoop WorkerEventLoop(selector,thread)
                .group(new NioEventLoopGroup())
                //3. 网络模型选择
                .channel(NioServerSocketChannel.class)
                //4. worker具体执行哪些逻辑（handler）
                .childHandler(
                        //5.代表和客户端进行数据读写的通道 Initializer初始化，负责添加别的handler
                        new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        //6.添加具体handler
                        channel.pipeline()
                                //将byteBuf转换为字符串
                                .addLast(new StringDecoder())
                                //自定义handler
                                .addLast(new ChannelInboundHandlerAdapter() {
                                    //读事件
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        System.out.println(msg);
                                        super.channelRead(ctx, msg);
                                    }
                                });
                    }
                    //7.绑定端口
                }).bind(8888);
    }
}
