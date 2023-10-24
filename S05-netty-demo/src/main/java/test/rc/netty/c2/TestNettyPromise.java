package test.rc.netty.c2;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;

import java.util.concurrent.ExecutionException;

public class TestNettyPromise {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //1.准备EventLoop对象
        EventLoop eventLoop = new NioEventLoopGroup().next();
        //2.可以主动创建promise 结果容器
        DefaultPromise<Integer> promise = new DefaultPromise<Integer>(eventLoop);
        new Thread(() -> {
            System.out.println("开始计算");
            try {
                int i = 1 / 0;
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                promise.setFailure(e);
                throw new RuntimeException(e);
            }
            promise.setSuccess(80);
        }).start();
        //接受结果的线程
        System.out.println("等待结果");

        System.out.println("结果是:" + promise.get());
    }
}
