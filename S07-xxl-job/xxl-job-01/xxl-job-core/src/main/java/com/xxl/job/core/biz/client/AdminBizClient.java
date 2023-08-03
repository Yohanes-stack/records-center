package com.xxl.job.core.biz.client;

import com.xxl.job.core.biz.AdminBiz;
import com.xxl.job.core.biz.model.RegistryParam;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.util.XxlJobRemotingUtil;

/**
 * 执行器访问调度中心客户端
 */
public class AdminBizClient implements AdminBiz {
    //调度中心的服务地址
    private String addressUrl;
    //token令牌，执行器和调度中心两端要一致
    private String accessToken;
    //访问超时时间
    private int timeout = 3;

    public AdminBizClient(String addressUrl, String accessToken) {
        this.addressUrl = addressUrl;
        this.accessToken = accessToken;
        if(!this.addressUrl.endsWith("/")){
            this.addressUrl = this.addressUrl + "/";
        }
    }

    @Override
    public ReturnT<String> registry(RegistryParam registryParam) {
        return XxlJobRemotingUtil.postBody(addressUrl + "api/registry",accessToken,timeout,registryParam,String.class);
    }

    @Override
    public ReturnT<String> registryRemove(RegistryParam registryParam) {
        return XxlJobRemotingUtil.postBody(addressUrl + "api/registryRemove",accessToken,timeout,registryParam,String.class);
    }
}
