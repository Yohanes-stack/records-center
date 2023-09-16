package com.xxl.job.core.thread;

import com.xxl.job.core.biz.AdminBiz;
import com.xxl.job.core.biz.model.RegistryParam;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.enums.RegistryConfig;
import com.xxl.job.core.executor.XxlJobExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * 执行器一端进行远程注册的类，将执行器注册到调度中心
 */
public class ExecutorRegistryThread {

    private static Logger logger = LoggerFactory.getLogger(ExecutorRegistryThread.class);

    //单例模式 饿汉式
    private static ExecutorRegistryThread instance = new ExecutorRegistryThread();

    public static ExecutorRegistryThread getInstance() {
        return instance;
    }

    //执行器注册到调度中心的线程 work thread.
    private Thread registryThread;
    //线程是否停止工作的标记
    private volatile boolean toStop = false;

    private ExecutorRegistryThread() {
    }

    public void start(final String appName, final String address) {
        if (appName == null || appName.trim().length() == 0) {
            logger.warn(">>>>>>>>>>> xxl-job, executor registry config fail, appName is null.");
            return;
        }
        //判断adminBizList集合不为空，因为客户端是用来和调度中心通信的
        if (XxlJobExecutor.getAdminBizList() == null) {
            logger.warn(">>>>>>>>>>> xxl-job, executor registry config fail, adminAddresses is null.");
            return;
        }
        registryThread = new Thread(() -> {
            //根据appName和address创建注册参数，注意这里的address是执行器的地址
            RegistryParam registryParam = new RegistryParam(RegistryConfig.RegistryType.EXECUTOR.name(), appName, address);

            //在一个循环中执行注册任务
            while (!toStop) {

                for (AdminBiz adminBiz : XxlJobExecutor.getAdminBizList()) {
                    try {
                        ReturnT<String> registryResult = adminBiz.registry(registryParam);
                        if (registryResult != null && ReturnT.SUCCESS_CODE == registryResult.getCode()) {
                            registryResult = ReturnT.SUCCESS;

                            logger.debug(">>>>>>>>>>> xxl-job registry success, registryParam:{}, registryResult:{}", new Object[]{registryParam, registryResult});
                            //注册成功则打破循环，注册成功一个后，调度中心就把相应的数据写到数据库中了
                            //直接推出循环即可
                            //注册不成功，再找下个注册中心继续注册
                            break;
                        } else {
                            //如果注册失败了，就寻找下一个调度中心继续注册
                            logger.info(">>>>>>>>>>> xxl-job registry fail, registryParam:{}, registryResult:{}", new Object[]{registryParam, registryResult});
                        }

                    } catch (Exception e) {
                        logger.info(">>>>>>>>>>> xxl-job registry error, registryParam:{}", registryParam, e);
                    }
                }
                //休眠30秒后再次循环重新注册，维护心跳
                try {
                    if (!toStop) {
                        //这里是每隔30秒，就再次循环重新注册一次，也就是维持心跳信息。
                        TimeUnit.SECONDS.sleep(RegistryConfig.BEAT_TIMEOUT);
                    }
                } catch (InterruptedException e) {
                    if (!toStop) {
                        logger.warn(">>>>>>>>>>> xxl-job, executor registry thread interrupted, error msg:{}", e.getMessage());
                    }
                }
            }

            //走到这里表示工作线程要结束了，不再继续提供服务注册和心跳服务了
            //所以要把注册的执行器信息从调度中心删除
            for (AdminBiz adminBiz : XxlJobExecutor.getAdminBizList()) {
                try {
                    //在这里发送删除执行器的信息
                    ReturnT<String> registryResult = adminBiz.registryRemove(registryParam);
                    if (registryResult != null && ReturnT.SUCCESS_CODE == registryResult.getCode()) {
                        registryResult = ReturnT.SUCCESS;
                        logger.info(">>>>>>>>>>> xxl-job registry-remove success, registryParam:{}, registryResult:{}", new Object[]{registryParam, registryResult});
                        break;
                    } else {
                        logger.info(">>>>>>>>>>> xxl-job registry-remove fail, registryParam:{}, registryResult:{}", new Object[]{registryParam, registryResult});
                    }
                } catch (Exception e) {
                    if (!toStop) {
                        logger.info(">>>>>>>>>>> xxl-job registry-remove error, registryParam:{}", registryParam, e);
                    }
                }
            }
            logger.info(">>>>>>>>>>> xxl-job, executor registry thread destroy.");
        });
        registryThread.setDaemon(true);
        registryThread.setName("xxl-job,executor ExecutorRegistryThread");
        registryThread.start();
    }

    public void toStop(){
        toStop = true;

        if(registryThread != null){
            //终端注册线程
            registryThread.interrupt();
            try {
                //阻塞等待注册线程执行
                registryThread.join();
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}