package com.xxl.job.core.server;

import com.xxl.job.core.biz.ExecutorBiz;
import com.xxl.job.core.biz.impl.ExecutorBizImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class EmbedServer {
    private static final Logger logger = LoggerFactory.getLogger(EmbedServer.class);

    //这个是执行器接口，但是在start方法中会被接口的实现类赋值
    private ExecutorBiz executorBiz;
    //启动Netty服务器的线程，说明内嵌服务器的启动也是异步的
    private Thread thread;
    public void start(String address, int port, String appName, String accessToken) {
        //当executorBiz赋值，这个ExecutorBizImpl就是用来执行定时任务的
        executorBiz = new ExecutorBizImpl();

    }
}
