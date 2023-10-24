package test.rc.webflux.handler;

import test.rc.webflux.vo.Message;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class MonoMessageHandler {

    public Mono<Message> echoHandler(Message message) {
        System.out.println(Thread.currentThread().getName() + "-" + message);
        message.setTitle("[" + Thread.currentThread().getName() + "]" + message.getTitle());
        message.setContent("[" + Thread.currentThread().getName() + "]" + message.getContent());
        return Mono.create(sink -> sink.success(message));
    }
}
