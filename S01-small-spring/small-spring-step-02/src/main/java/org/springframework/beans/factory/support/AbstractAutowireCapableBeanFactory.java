package org.springframework.beans.factory.support;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;

public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory{
    @Override
    protected Object createBean(String beanName, BeanDefinition beanDefinition) throws BeansException {
        Object bean;
        try {
            bean = beanDefinition.getBeanClass().newInstance();
        }  catch (InstantiationException | IllegalAccessException e) {
            throw new BeansException("Instantiation of bean failed", e);
        }

        addSingleton(beanName,bean);
        return bean;
    }
}
