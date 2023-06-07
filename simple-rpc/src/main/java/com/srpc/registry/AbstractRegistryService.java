package com.srpc.registry;

import com.srpc.configuration.RpcProperties;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

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
