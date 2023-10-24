package test.rc.netty.c2;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class TestNettyFuture {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        EventLoop eventLoop = group.next();
        Future<Object> submit = eventLoop.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                System.out.println("执行计算");
                Thread.sleep(3000);
                return 70;
            }
        });
//        System.out.println("等待结果");
//        System.out.println("结果是："+submit.get());
        submit.addListener(new GenericFutureListener<Future<? super Object>>() {
            @Override
            public void operationComplete(Future<? super Object> future) throws Exception {
                System.out.println("接受结果:" + future.getNow());
            }
        });
    }
}
