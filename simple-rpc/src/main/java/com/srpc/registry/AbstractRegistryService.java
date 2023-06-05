package com.srpc.registry;

import com.srpc.common.ServiceMeta;
import com.srpc.config.RpcProperties;
import com.srpc.registry.loadbalancer.LoadBalancerType;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.IOException;
import java.util.List;

/**
 * 注册中心抽象层接口
 */
public abstract class AbstractRegistryService implements RegistryService, ApplicationContextAware {


    protected RpcProperties rpcProperties;

    protected ApplicationContext applicationContext;

    protected String buildServiceKey(String serviceName, String serviceVersion) {
        return String.join("$", serviceName, serviceVersion);
    }


    public AbstractRegistryService() {
        rpcProperties = this.getBean(RpcProperties.class);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    protected <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }
}
