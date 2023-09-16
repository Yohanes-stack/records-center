package com.xxl.job.core.handler;

public abstract class IJobHandler {
    public abstract void execute() throws Exception;

    public abstract void init() throws Exception;

    public abstract void destroy() throws Exception;

}
