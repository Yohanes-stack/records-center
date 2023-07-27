package org.springframework.beans.factory.config;

import org.springframework.beans.factory.ConfigurableListableBeanFactory;

/**
 * 允许自定义修改BeanDefinition属性信息
 */
public interface BeanFactoryPostProcessor {

    /**
     * 在所有的BeanDefinition 加载完成后，实例化Bean对象之前，提供修改BeanDefinition 属性的机制
     * @param beanFactory
     */
    void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory);
}
