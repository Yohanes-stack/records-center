package com.xxl.job.core.biz.model;

import java.io.Serializable;

public class TriggerParam implements Serializable {
    private static final long serialVersionUID = 42L;
    //定时任务id
    private int jobId;
    //JobHandler的名称
    private String executorHandler;
    //定时任务的参数
    private String executorParams;
    //阻塞策略
    private String executorBlockStrategy;
    //超时事件
    private int executorTimeout;
    //日志id
    private long logId;
    //日志时间
    private long logDateTime;
    //运行模式
    private String glueType;
    //代码文本
    private String glueSource;
    //glue 更新时间
    private long glueUpdateTime;
    //分片索引
    private int broadcastIndex;
    //分片总数
    private int broadcastTotal;

    public int getJobId() {
        return jobId;
    }

    public TriggerParam setJobId(int jobId) {
        this.jobId = jobId;
        return this;
    }

    public String getExecutorHandler() {
        return executorHandler;
    }

    public TriggerParam setExecutorHandler(String executorHandler) {
        this.executorHandler = executorHandler;
        return this;
    }

    public String getExecutorParams() {
        return executorParams;
    }

    public TriggerParam setExecutorParams(String executorParams) {
        this.executorParams = executorParams;
        return this;
    }

    public String getExecutorBlockStrategy() {
        return executorBlockStrategy;
    }

    public TriggerParam setExecutorBlockStrategy(String executorBlockStrategy) {
        this.executorBlockStrategy = executorBlockStrategy;
        return this;
    }

    public int getExecutorTimeout() {
        return executorTimeout;
    }

    public TriggerParam setExecutorTimeout(int executorTimeout) {
        this.executorTimeout = executorTimeout;
        return this;
    }

    public long getLogId() {
        return logId;
    }

    public TriggerParam setLogId(long logId) {
        this.logId = logId;
        return this;
    }

    public long getLogDateTime() {
        return logDateTime;
    }

    public TriggerParam setLogDateTime(long logDateTime) {
        this.logDateTime = logDateTime;
        return this;
    }

    public String getGlueType() {
        return glueType;
    }

    public TriggerParam setGlueType(String glueType) {
        this.glueType = glueType;
        return this;
    }

    public String getGlueSource() {
        return glueSource;
    }

    public TriggerParam setGlueSource(String glueSource) {
        this.glueSource = glueSource;
        return this;
    }

    public long getGlueUpdateTime() {
        return glueUpdateTime;
    }

    public TriggerParam setGlueUpdateTime(long glueUpdateTime) {
        this.glueUpdateTime = glueUpdateTime;
        return this;
    }

    public int getBroadcastIndex() {
        return broadcastIndex;
    }

    public TriggerParam setBroadcastIndex(int broadcastIndex) {
        this.broadcastIndex = broadcastIndex;
        return this;
    }

    public int getBroadcastTotal() {
        return broadcastTotal;
    }

    public TriggerParam setBroadcastTotal(int broadcastTotal) {
        this.broadcastTotal = broadcastTotal;
        return this;
    }
}
