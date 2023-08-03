package com.xxl.job.core.biz;

import com.xxl.job.core.biz.model.RegistryParam;
import com.xxl.job.core.biz.model.ReturnT;

import javax.xml.transform.Result;

public interface AdminBiz {

    /**
     * 执行器注册自己到调度中心
     * @param registryParam
     * @return
     */
    ReturnT<String> registry(RegistryParam registryParam);

    /**
     * 执行器将自己从调度中心移除
     * @param registryParam
     * @return
     */
    ReturnT<String> registryRemove(RegistryParam registryParam);
}
