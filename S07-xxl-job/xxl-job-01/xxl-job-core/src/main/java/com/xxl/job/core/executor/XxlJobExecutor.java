package com.xxl.job.core.executor;

import com.xxl.job.core.biz.AdminBiz;
import com.xxl.job.core.biz.client.AdminBizClient;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.handler.impl.MethodJobHandler;
import com.xxl.job.core.server.EmbedServer;
import com.xxl.job.core.thread.JobThread;
import com.xxl.job.core.util.IpUtil;
import com.xxl.job.core.util.NetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class XxlJobExecutor {
    private static final Logger logger = LoggerFactory.getLogger(XxlJobExecutor.class);
    //服务器的地址，也就是调度中心的地址
    private String adminAddress;
    //token令牌，这个令牌要和调度中心的令牌一致，否则调度中心校验报错
    private String accessToken;
    //执行器名称
    private String appName;
    //执行器的地址，如果为空，使用默认地址 。 格式: ip+port
    private String address;
    //注册器的ip
    private String ip;
    //端口号
    private int port;
    //执行器的日志收集地址
    private String logPath;
    //执行器日志的保留天数，一般为30天
    private int logRetentionDays;



    //该成员变量是用来存放AdminBizClient对象的，而该对象是用来向调度中心发送注册信息的
    private static List<AdminBiz> adminBizList;
    //服务器独享
    private EmbedServer embedServer = null;


    /**
     * 执行器的注册中心
     */
    private static ConcurrentMap<String, IJobHandler> jobHandlerRepository = new ConcurrentHashMap<>();

    /**
     * 定时任务中心
     */
    private static ConcurrentMap<Integer, JobThread> jobThreadRepository = new ConcurrentHashMap<>();



    public void start() {
        //初始化地址存储集合
        //在集群环境下，会有多个调度中心，所以，执行器要把自己分别注册到这些调度中心里
        initAdminBizList(adminAddress, accessToken);
        //启动执行器内部内嵌的服务器，该服务器是用Netty构建的，但构建的是Http服务器
        //在该方法中，会进一步把执行器注册到调度中心上
        initEmbedServer(address, ip, port, appName, accessToken);
    }

    private void initEmbedServer(String address, String ip, int port, String appName, String accessToken) {
        //
        port = port > 0 ? port : NetUtil.findAvailablePort(9999);
        //
        ip = ip != null && ip.trim().length() > 0 ? ip : IpUtil.getIp();
        //判断地址是否为null
        if (address == null || address.trim().length() == 0) {
            //如果为空说明没有配置，就把刚刚得到的ip和地址拼接起来
            String ip_port_address = IpUtil.getIpPort(ip, port);
            address = "http://{ip_port}/".replace("{ip_port}", ip_port_address);
        }
        //校验token
        if (accessToken == null || accessToken.trim().length() == 0) {
            logger.warn(">>>>>>>>>>> xxl-job accessToken is empty. To ensure system security, please set the accessToken.");
        }
        //创建执行器短的Netty服务器
        embedServer = new EmbedServer();
        embedServer.start(address,port,appName,accessToken);
    }

    private void initAdminBizList(String adminAddress, String accessToken) {
        if (adminAddress != null && adminAddress.trim().length() > 0) {
            for (String address : adminAddress.trim().split(",")) {
                AdminBiz adminBiz = new AdminBizClient(address.trim(), accessToken);
                if (adminBizList == null) {
                    adminBizList = new ArrayList<AdminBiz>();
                }
                adminBizList.add(adminBiz);
            }
        }
    }

    public void destroy(){
        //停止内嵌服务器
        stopEmbedServer();
    }

    private void stopEmbedServer() {
        if(embedServer != null){
            embedServer.stop();
        }
    }


    public static IJobHandler loadJobHandler(String name){
        return jobHandlerRepository.get(name);
    }

    public static IJobHandler registryJobHandler(String name,IJobHandler handler){
        logger.info(">>>>>>>>>>> xxl-job register jobhandler success, name:{}, jobHandler:{}", name, handler);
        return jobHandlerRepository.put(name,handler);
    }


    protected void registryJobHandler(XxlJob xxlJob, Object bean, Method executeMethod){
        if(xxlJob == null){
            return;
        }

        String name = xxlJob.value();
        Class<?> clazz = bean.getClass();
        String methodName = executeMethod.getName();
        //对定时任务的名称做判空处理
        if (name.trim().length() == 0) {
            throw new RuntimeException("xxl-job method-jobhandler name invalid, for[" + clazz + "#" + methodName + "] .");
        }

        //从缓存JobHandler的Map中，根据定时任务的名字获取JobHandler
        if (loadJobHandler(name) != null) {
            //如果不为空，说明已经存在相同名字的定时任务了，也有了对应的JobHandler了，所以抛出异常
            throw new RuntimeException("xxl-job jobhandler[" + name + "] naming conflicts.");
        }

        //设置方法可访问
        executeMethod.setAccessible(true);


        Method initMethod = null;
        Method destroyMethod = null;

        if(xxlJob.init().trim().length() > 0){
            try {
                //如果有则使用反射从bean对象中获得相应的初始化方法
                initMethod = clazz.getDeclaredMethod(xxlJob.init());
                //设置可访问，因为后续会根据反射调用的
                initMethod.setAccessible(true);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("xxl-job method-jobhandler initMethod invalid, for[" + clazz + "#" + methodName + "] .");
            }
        }

        //判断有没有定义销毁的方法
        if (xxlJob.destroy().trim().length() > 0) {
            try {
                //如果有就使用反射获得
                destroyMethod = clazz.getDeclaredMethod(xxlJob.destroy());
                //设置可访问
                destroyMethod.setAccessible(true);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("xxl-job method-jobhandler destroyMethod invalid, for[" + clazz + "#" + methodName + "] .");
            }
        }

        registryJobHandler(name,new MethodJobHandler(bean,executeMethod,initMethod,destroyMethod));



    }

    public static JobThread registryJobThread(int jobId,IJobHandler handler,String removeOldReason){
        JobThread newJobThread = new JobThread(jobId,handler);
        newJobThread.start();
        logger.info(">>>>>>>>>>> xxl-job regist JobThread success, jobId:{}, handler:{}", new Object[]{jobId, handler});
        //将该线程缓存到Map中
        JobThread oldJobThread = jobThreadRepository.put(jobId, newJobThread);
        if (oldJobThread != null) {
            //如果oldJobThread不为null，说明Map中已经缓存了相同的对象
            //这里的做法就是直接停止旧线程
            oldJobThread.toStop(removeOldReason);
            oldJobThread.interrupt();
        }
        return newJobThread;
    }

    public static JobThread removeJobThead(int jobId,String removeOldReason){
        JobThread oldJobThread = jobThreadRepository.remove(jobId);
        if (oldJobThread != null) {
            //停止线程，在该方法内部，会讲线程的停止条件设为true，线程就会停下了
            oldJobThread.toStop(removeOldReason);
            oldJobThread.interrupt();
            return oldJobThread;
        }
        return null;
    }

    public static JobThread loadJobThread(int jobId){
        return jobThreadRepository.get(jobId);
    }


    public static List<AdminBiz> getAdminBizList() {
        return adminBizList;
    }

    public String getAdminAddress() {
        return adminAddress;
    }

    public XxlJobExecutor setAdminAddress(String adminAddress) {
        this.adminAddress = adminAddress;
        return this;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public XxlJobExecutor setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }

    public String getAppName() {
        return appName;
    }

    public XxlJobExecutor setAppName(String appName) {
        this.appName = appName;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public XxlJobExecutor setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getIp() {
        return ip;
    }

    public XxlJobExecutor setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public int getPort() {
        return port;
    }

    public XxlJobExecutor setPort(int port) {
        this.port = port;
        return this;
    }

    public String getLogPath() {
        return logPath;
    }

    public XxlJobExecutor setLogPath(String logPath) {
        this.logPath = logPath;
        return this;
    }

    public int getLogRetentionDays() {
        return logRetentionDays;
    }

    public XxlJobExecutor setLogRetentionDays(int logRetentionDays) {
        this.logRetentionDays = logRetentionDays;
        return this;
    }
}
