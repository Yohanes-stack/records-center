package test.rc.netty.c2;

import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.NettyRuntime;

import java.util.concurrent.TimeUnit;

public class TestEventLoop {
    public static void main(String[] args) {
        //1.创建事件循环组
        EventLoopGroup group = new NioEventLoopGroup(2); //io事件，普通任务 定时任务
        //普通任务 定时任务
//        DefaultEventLoopGroup eventExecutors = new DefaultEventLoopGroup();
        System.out.println(NettyRuntime.availableProcessors());
        //2.获取下一个事件循环对象
        System.out.println(group.next());
        System.out.println(group.next());
        System.out.println(group.next());
        //3.执行普通任务
        group.next().submit(() -> System.out.println("ok"));

        //4.执行定时任务
        group.next().scheduleAtFixedRate(() -> {
            System.out.println("schedule");
        }, 0,1, TimeUnit.SECONDS);
        System.out.println("main");

    }
}
