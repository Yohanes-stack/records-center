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
import java.util.Scanner;

public class CloseFutureClient {
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
        Channel channel = channelFuture.sync().channel();
        new Thread(()->{
            Scanner sc = new Scanner(System.in);
            while(true){
                String line = sc.nextLine();
                if("q".equals(line)){
                    channel.close();
                    System.out.println("关闭之后");
                    break;
                }
                channel.writeAndFlush(line);
            }
        },"input").start();

        //获取closeFuture对象 1.同步处理关闭 2.异步处理关闭
        ChannelFuture closeFuture = channel.closeFuture();
//        System.out.println("waiting close 。。。");
//        closeFuture.sync();
//        System.out.println("处理关闭之后的操作");
        closeFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                System.out.println("处理关闭之后的操作");
            }
        });
    }

}
