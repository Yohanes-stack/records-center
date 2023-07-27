package org.springframework.test;

import org.junit.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.test.bean.TestService;

public class beanTest {
    @Test
    public void BeanFactory(){
        //初始化BeanFactory
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        //注册Bean信息
        BeanDefinition beanDefinition = new BeanDefinition(TestService.class);
        beanFactory.registerBeanDefinition("testService",beanDefinition);

        //获取bean
        TestService testService = (TestService)beanFactory.getBean("testService");
        testService.get();

        TestService singleTestService = (TestService)beanFactory.getSingleton("testService");
        singleTestService.get();

    }
}
