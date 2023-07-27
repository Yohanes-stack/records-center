package com.power.gateway.test;

import com.power.gateway.ssesion.Configuration;
import com.power.gateway.ssesion.GenericReferenceSessionFactoryBuilder;
import io.netty.channel.Channel;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * 配置项
 */
public class ApiTest {

    private final Logger logger = LoggerFactory.getLogger(ApiTest.class);
    @Test
    public void test_GenericReference() throws ExecutionException, InterruptedException {
        Configuration configuration = new Configuration();
        configuration.addGenericReference("api-gateway-test","com.power.gateway.rpc.test","sayHi");

        GenericReferenceSessionFactoryBuilder builder = new GenericReferenceSessionFactoryBuilder();
        Future<Channel> future = builder.build(configuration);

        logger.info("服务启动完成:{}",future.get().id());

        Thread.sleep(Long.MAX_VALUE);
    }

}
