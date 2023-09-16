package com.xxl.job.core.thread;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.biz.model.TriggerParam;
import com.xxl.job.core.executor.XxlJobExecutor;
import com.xxl.job.core.handler.IJobHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.Result;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public class JobThread extends Thread {

    private static Logger logger = LoggerFactory.getLogger(JobThread.class);

    private int jobId;
    /**
     * 触发器
     */

    private IJobHandler handler;
    /**
     * 存放触发器参数的一个队列
     */
    private LinkedBlockingDeque<TriggerParam> triggerQueue;

    /**
     * 线程终止的标志
     */
    private volatile boolean toStop = false;
    /**
     * 线程执行的原因
     */
    private String stopReason;
    /**
     * 线程是否正在工作的标记
     */
    private boolean running = false;
    /**
     * 该线程的空闲时间，默认为0
     */
    private int idleTimes = 0;


    public JobThread(int jobId, IJobHandler handler) {
        this.jobId = jobId;
        this.handler = handler;
        this.triggerQueue = new LinkedBlockingDeque<>();
        //设置工作线程名称
        this.setName("xxl-job, JobThread-" + jobId + "-" + System.currentTimeMillis());

    }

    public IJobHandler getHandler() {
        return handler;
    }

    public ReturnT<String> pushTriggerQueue(TriggerParam triggerParam) {
        //放入队列
        triggerQueue.add(triggerParam);

        return ReturnT.SUCCESS;
    }

    public void toStop(String stopReason) {
        this.toStop = true;
        this.stopReason = stopReason;
    }

    public boolean isRunningOrHasQueue() {
        return running || triggerQueue.size() > 0;
    }


    @Override
    public void run() {
        try {
            handler.init();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        while (!toStop) {
            //这个线程执行完以后设置为false，表示任务 未开始、或者是任务没结束
            running = false;
            //线程的等待时间，说明在多少次以内，队列里没有数据
            idleTimes++;

            TriggerParam triggerParam = null;

            try {
                triggerParam = triggerQueue.poll(3L, TimeUnit.SECONDS);
                if (triggerParam != null) {
                    running = true;
                    //空闲时间
                    idleTimes = 0;
                    handler.execute();
                } else {
                    if(idleTimes > 30){
                        if(triggerQueue.size() == 0){
                            XxlJobExecutor.
                        }
                    }
                }

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }
}
