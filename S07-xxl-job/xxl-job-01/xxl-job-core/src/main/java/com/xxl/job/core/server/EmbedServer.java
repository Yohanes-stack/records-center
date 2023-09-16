package com.xxl.job.core.server;

import com.xxl.job.core.biz.ExecutorBiz;
import com.xxl.job.core.biz.impl.ExecutorBizImpl;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.biz.model.TriggerParam;
import com.xxl.job.core.thread.ExecutorRegistryThread;
import com.xxl.job.core.util.GsonTool;
import com.xxl.job.core.util.XxlJobRemotingUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;


public class EmbedServer {
    private static final Logger logger = LoggerFactory.getLogger(EmbedServer.class);

    //这个是执行器接口，但是在start方法中会被接口的实现类赋值
    private ExecutorBiz executorBiz;
    //启动Netty服务器的线程，说明内嵌服务器的启动也是异步的
    private Thread thread;

    /**
     * 创建netty线程
     * 线程池， 核心为0 最大为200 ，队列为2000，规范线程池名字以及自定义拒绝策略为
     * @param address
     * @param port
     * @param appName
     * @param accessToken
     */
    public void start(String address, int port, String appName, String accessToken) {
        //当executorBiz赋值，这个ExecutorBizImpl就是用来执行定时任务的
        executorBiz = new ExecutorBizImpl();
        //创建线程，在线程中启动Netty服务器
        thread = new Thread(() -> {
            NioEventLoopGroup bossGroup = new NioEventLoopGroup();
            NioEventLoopGroup workerGroup = new NioEventLoopGroup();
            //bizThreadPool线程池会传入到下面的EmbedHttpServerHandler入站
            ThreadPoolExecutor bizThreadPool = new ThreadPoolExecutor(
                    0,
                    200,
                    60L,
                    TimeUnit.SECONDS,
                    new LinkedBlockingDeque<>(2000),
                    new ThreadFactory() {
                        @Override
                        public Thread newThread(Runnable r) {
                            return new Thread(r, "xxl-job ，EmbedServer bizThreadPool-" + r.hashCode());
                        }
                    },
                    new RejectedExecutionHandler() {
                        @Override
                        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                            throw new RuntimeException("xxl-job, EmbedServer bizThreadPool is EXHAUSTED!");
                        }
                    }
            );


            try {
                ServerBootstrap bootstrap = new ServerBootstrap();
                bootstrap.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel channel) throws Exception {
                                channel.pipeline()
                                        //心跳
                                        .addLast(new IdleStateHandler(0, 0, 30 * 3, TimeUnit.SECONDS))
                                        //http编解码器，出站和入站处理器
                                        .addLast(new HttpServerCodec())
                                        //消息聚合处理器 拆分包5兆为一个消息体
                                        .addLast(new HttpObjectAggregator(5 * 1024 * 1024))
                                        //入站处理器，可以在该处理器中执行定时任务
                                        .addLast(new EmbedHttpServerHandler(executorBiz, accessToken, bizThreadPool));
                            }
                        })
                        .childOption(ChannelOption.SO_KEEPALIVE, true);

                ChannelFuture future = bootstrap.bind(port).sync();
                logger.info(">>>>>>>>>>> xxl-job remoting server start success, nettype = {}, port = {}", EmbedServer.class, port);
                startRegistry(appName,address);
            } catch (InterruptedException e) {

            }
        });


    }

    private void startRegistry(String appName, String address) {
        ExecutorRegistryThread.getInstance().start(appName,address);
    }

    public void stop() {
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
        }
        //销毁注册执行器到调度中心的线程
        stopRegistry();

        logger.info(">>>>>>>>>>> xxl-job remoting server destroy success.");

    }


    private void stopRegistry() {
        ExecutorRegist
    }

    public static class EmbedHttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

        private static final Logger logger = LoggerFactory.getLogger(EmbedHttpServerHandler.class);

        //远程调用
        private ExecutorBiz executorBiz;

        private String accessToken;

        private ThreadPoolExecutor bizThreadPool;

        public EmbedHttpServerHandler(ExecutorBiz executorBiz, String accessToken, ThreadPoolExecutor threadPoolExecutor) {
            this.executorBiz = executorBiz;
            this.accessToken = accessToken;
            this.bizThreadPool = threadPoolExecutor;
        }


        @Override
        protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
            String requestData = msg.content().toString(CharsetUtil.UTF_8);

            String uri = msg.uri();

            HttpMethod method = msg.method();

            //判断http链接是否存活
            boolean keepAlive = HttpUtil.isKeepAlive(msg);

            String accessTokenReq = msg.headers().get(XxlJobRemotingUtil.XXL_JOB_ACCESS_TOKEN);

            bizThreadPool.execute(() -> {
                process(method, uri, requestData, accessTokenReq);
            });


        }

        private Object process(HttpMethod method, String uri, String requestData, String accessToken) {
            //判断是不是post方法
            if (HttpMethod.POST != method) {
                return new ReturnT<String>(ReturnT.FAIL_CODE, "invalid request, HttpMethod not support.");
            }
            //校验uri是否为空
            if (uri == null || uri.trim().length() == 0) {
                return new ReturnT<String>(ReturnT.FAIL_CODE, "invalid request, uri-mapping empty.");
            }
            //判断执行器令牌是否和调度中心令牌一样，这里也能发现，调度中心和执行器的token令牌一定要是相等的，因为判断是双向的，两边都要判断
            if (accessToken != null
                    && accessToken.trim().length() > 0
                    && !accessToken.equals(accessToken)) {
                return new ReturnT<String>(ReturnT.FAIL_CODE, "The access token is wrong.");
            }
            try {
                //开始从uri中具体判断，调度中心触发的是什么任务了
                switch (uri) {
                    case "/run":
                        //run就意味着是要执行定时任务
                        //把requestData转化成触发器参数对象，也就是TriggerParam对象
                        TriggerParam triggerParam = GsonTool.fromJson(requestData, TriggerParam.class);
                        //然后交给ExecutorBizImpl对象去执行定时任务
                        return executorBiz.run(triggerParam);
                    default:
                        return new ReturnT<String>(ReturnT.FAIL_CODE, "invalid request, uri-mapping(" + uri + ") not found.");
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                return new ReturnT<String>(ReturnT.FAIL_CODE, "request error:" + ThrowableUtil.toString(e));
            }

        }


    }


}
