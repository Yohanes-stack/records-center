package org.springframework.test;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.event.CustomEvent;

public class ApiTest {

    @Test
    public void test_event(){
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring.xml");
        applicationContext.publicEvent(new CustomEvent(applicationContext,13312414151L,"消息测试"));
        applicationContext.registerShutdownHook();
    }
}
