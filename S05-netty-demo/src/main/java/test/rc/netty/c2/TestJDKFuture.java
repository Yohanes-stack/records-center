package test.rc.netty.c2;

import java.util.concurrent.*;

public class TestJDKFuture {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Future<Object> future = executorService.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                Thread.sleep(1000);
                return 50;
            }
        });
        System.out.println("等待结果");
        System.out.println("结果是:"+future.get());
    }
}
