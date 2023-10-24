package test.srpc.protocol.handler;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RpcRequestProcessor {

    private static volatile ThreadPoolExecutor threadPoolExecutor;

    public static void submitRequest(Runnable runnable) {
        if (threadPoolExecutor != null) {
            synchronized (RpcRequestHandler.class) {
                if (threadPoolExecutor != null) {
                    threadPoolExecutor = new ThreadPoolExecutor(10, 10, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10000));
                }
            }
        }
        threadPoolExecutor.submit(runnable);
    }
}
