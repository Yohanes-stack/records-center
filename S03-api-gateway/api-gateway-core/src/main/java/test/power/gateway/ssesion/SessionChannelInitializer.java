package test.power.gateway.ssesion;

import test.power.gateway.ssesion.handler.SessionServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseDecoder;

public class SessionChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        channel.pipeline().addLast(new HttpRequestDecoder())
                .addLast(new HttpResponseDecoder())
                .addLast(new HttpObjectAggregator(1024*1024))
                .addLast(new SessionServerHandler());

    }
}
