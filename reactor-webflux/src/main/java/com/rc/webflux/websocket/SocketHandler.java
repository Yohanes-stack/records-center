package com.rc.webflux.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

@Component
public class SocketHandler implements WebSocketHandler {
    @Override
    public Mono<Void> handle(WebSocketSession session) {
        System.out.println("Websocket客户端握手信息" + session.getHandshakeInfo().getUri());
        return session.send(session.receive().map(msg -> session.textMessage("[socket]" + msg)));
    }
}
