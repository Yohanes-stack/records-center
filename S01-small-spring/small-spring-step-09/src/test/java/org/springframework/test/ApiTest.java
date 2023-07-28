package org.springframework.test;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.bean.TestDao;

public class ApiTest {

    @Test
    public void test_factory_bean(){
        //1.初始化 BeanFactory
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring.xml");
        applicationContext.registerShutdownHook();

        //2.调用代理方法

        TestDao testDao = applicationContext.getBean("proxyUserDao", TestDao.class);

        System.out.println(testDao.queryUserName("10001"));
    }
}
