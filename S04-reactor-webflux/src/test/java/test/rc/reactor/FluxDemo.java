package test.rc.reactor;

import org.junit.Test;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class FluxDemo {

    /**
     * Flux事件流的几种创建方式
     *
     * @throws InterruptedException
     */
    @Test
    public void simpleCreateTest() throws InterruptedException {
        //创建Flux的几种方式
        //subscribe(System.out::println) 就是打印结果

        //可以指定序列中包含的全部元素。创建出来的 Flux 序列在发布这些元素之后会自动结束
        Flux<Object> just = Flux.just(new Object());
        print(just);
        //可以从一个数组、Iterable 对象或 Stream 对象中创建 Flux 对象
        Flux<String> stringFlux = Flux.fromArray(new String[]{
                "1", "2"
        });
        print(stringFlux);
        //创建一个不包含任何元素，只发布结束消息的序列  当您需要一个空的Flux时，如果您希望它可以被取消或结束，请使用empty
        Flux<Object> empty = Flux.empty();
        print(empty);
        //创建一个只包含错误消息的序列
        Flux<Object> error = Flux.error(new Exception());
        print(error);
        //创建一个不包含任何消息通知的序列，如果需要一个无限期等待的Flux 使用never
        Flux<Object> never = Flux.never();
        print(never);
        //创建包含从 start 起始的 count 个数量的 Integer 对象的序列
        Flux<Integer> range = Flux.range(0, 10);
        print(range);
        //Flux.interval是一个用于生成间隔时间事件的方法。它会按照指定的时间间隔生成一个递增的Long类型序列，
        // 并将其包装为一个Flux流对象。可以使用该方法来定时触发事件，比如周期性地发送心跳包或轮询外部接口。
        //
        Flux<Long> map = Flux.interval(Duration.ofSeconds(1));
        print(map);

        Thread.sleep(5000);
        //上面的这些静态方法适合于简单的序列生成，当序列的生成需要复杂的逻辑时，则应该使用 generate() 或 create() 方法。


    }

    @Test
    public void generateTest() {
        Flux.generate(sink -> {
            //next 用于通知订阅者
            sink.next("Hello？");
            //complete表示完成事件
            sink.complete();
        }).subscribe(System.out::println);

        //stateSupplier 是一个函数，用于提供初始状态。它返回一个类型为 S 的对象，表示当前状态。
        //generator 是一个具有两个参数的函数，第一个参数是当前状态 S，
        //第二个参数是 SynchronousSink 对象。SynchronousSink 用于发出下游请求的结果。generator 函数根据当前状态生成下一个值，并将其提交给 SynchronousSink，然后返回更新后的状态 S。如果要终止流，则可以调用 SynchronousSink 的 complete 方法。
        Flux.generate(() -> 0, (state, sink) -> {
            //next 用于通知订阅者
            sink.next(state);
            //complete表示完成事件
            if (state == 10) {
                sink.complete();
            }
            return state + 1;
        }).subscribe(System.out::println);
    }

    @Test
    public void create() {
        Flux.create(sink -> {
            for (int i = 0; i < 10; i++) {
                sink.next(i);
            }
            sink.complete();
        }).subscribe(System.out::println);
    }

    @Test
    public void buffer() {
        //buffer是将，flux队列 缓存在一个list集合中，到最大个数时，一次性提供出去
        //这里模拟，相隔2秒，给出20个数字
        Flux.create(fluxSink -> {
            for (int i = 0; i < 5; i++) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                for (int i1 = 0; i1 < 20; i1++) {
                    fluxSink.next(i1);
                }
            }
            fluxSink.complete();
        }).buffer(20).subscribe(System.out::println);
    }

    public void print(Flux flux) {
        flux.subscribe(System.out::println);
    }

}
