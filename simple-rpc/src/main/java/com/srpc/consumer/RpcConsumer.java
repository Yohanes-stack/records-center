package com.srpc.consumer;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;

public class RpcConsumer {

    private final Bootstrap bootstrap;

    private final EventLoopGroup eventLoopGroup;

    public RpcConsumer(Bootstrap bootstrap, EventLoopGroup eventLoopGroup) {
        this.bootstrap = bootstrap;
        this.eventLoopGroup = eventLoopGroup;
    }
}
