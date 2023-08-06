package com.xxl.job.core.biz;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.biz.model.TriggerParam;

public interface ExecutorBiz {

    ReturnT<String> run(TriggerParam triggerParam);
}
