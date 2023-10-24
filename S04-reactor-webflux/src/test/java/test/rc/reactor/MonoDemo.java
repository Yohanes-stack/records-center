package test.rc.reactor;

import org.junit.Test;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class MonoDemo {
    @Test
    public void fromCallableTest() {
        Mono.fromCallable(() -> new Date()).subscribe(System.out::println);
    }

    @Test
    public void fromCompletionStageTest() throws InterruptedException {
        // Creating a CompletableFuture
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            // Simulating a long-running task
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "Hello, world!";
        });

        // Converting the CompletableFuture to Mono using Mono.fromCompletionStage()
        Mono<String> monoCompletableFuture = Mono.fromCompletionStage(completableFuture);
        Mono<String> monoFuture = Mono.fromFuture(completableFuture);
        // Subscribing to the Mono
        monoCompletableFuture.subscribe(System.out::println);
        monoFuture.subscribe(System.out::println);
        Thread.sleep(6000);
    }

    @Test
    public void fromSupplierTest() {
        Mono.fromSupplier(() -> "Hello world").subscribe(System.out::println);
    }

    @Test
    public void fromRunnableTest() {
        Mono.fromRunnable(() -> {
            // 这里放需要执行的代码逻辑
            System.out.println("Hello, world!");
        }).subscribe(result -> System.out.println("Execution completed."));
    }

    @Test
    public void justOrEmptyTest() {
        //从一个 Optional 对象或可能为 null 的对象中创建 Mono。只有 Optional 对象中包含值或对象不为 null 时，Mono 序列才产生对应的元素。
        Mono<Object> objectMono = Mono.justOrEmpty(Optional.empty());
        objectMono.subscribe(System.out::println);
    }
}
