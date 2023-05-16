//package com.rc.webflux.router;
//
//import com.rc.webflux.handler.MessageHandler;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.MediaType;
//import org.springframework.web.reactive.function.server.RequestPredicates;
//import org.springframework.web.reactive.function.server.RouterFunction;
//import org.springframework.web.reactive.function.server.RouterFunctions;
//import org.springframework.web.reactive.function.server.ServerResponse;
//
///**
// * 消息路由处理
// */
//@Configuration
//public class MessageRouter {
//
//    @Bean
//    public RouterFunction<ServerResponse> routeEach(MessageHandler messageHandler) {
//        //匹配路由地址
//        //绑定的访问模式为GET请求，具体的访问地址"/echo"
//        return RouterFunctions.route(RequestPredicates.GET("/echo")
//                //设置了请求类型
//                .and(RequestPredicates.accept(MediaType.TEXT_PLAIN))
//                , messageHandler::echoHandler);
//    }
//
//}
