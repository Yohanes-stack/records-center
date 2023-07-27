package org.springframework.test;

import org.junit.Test;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanReference;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.bean.TestDao;
import org.springframework.test.bean.TestService;

public class ApiTest {

    @Test
    public void BeanFactory() {
        //初始化BeanFactory
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        //注册Bean信息
        beanFactory.registerBeanDefinition("testDao", new BeanDefinition(TestDao.class));

        PropertyValues propertyValues = new PropertyValues();
        propertyValues.addPropertyValue(new PropertyValue("id", "10001"));
        propertyValues.addPropertyValue(new PropertyValue("testDao", new BeanReference("testDao")));

        BeanDefinition beanDefinition = new BeanDefinition(TestService.class, propertyValues);
        beanFactory.registerBeanDefinition("testService",beanDefinition);
        //获取bean
        TestService testService = (TestService) beanFactory.getBean("testService");
        System.out.println(testService.getTestDao().queryUserName(testService.getId()));

        TestService singleTestService = (TestService) beanFactory.getSingleton("testService");

        System.out.println(singleTestService.getTestDao().queryUserName(testService.getId()));

    }

    @Test
    public void test_xml(){
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:springPostProcessor.xml");
        TestService testService = applicationContext.getBean("testService", TestService.class);
        System.out.println(testService.getTestDao().queryUserName(testService.getId()));
        System.out.println(testService.getId());
        System.out.println(testService.getName());

    }
}
