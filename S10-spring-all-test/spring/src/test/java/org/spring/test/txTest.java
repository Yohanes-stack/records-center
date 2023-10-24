package org.spring.test;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 事务测试
 */
public class txTest {

    private ApplicationContext applicationContext1;

    private ApplicationContext applicationContext2;
    @Before
    public void startup() {
        applicationContext1 = new ClassPathXmlApplicationContext("applicationContext.xml");
        applicationContext2 = new ClassPathXmlApplicationContext("applicationContext1.xml");
    }

    @Test
    public void startTransactional() {
        UserService bean = applicationContext1.getBean(UserService.class);
        System.out.println(bean);
        UserService bean1 = applicationContext2.getBean(UserService.class);
        System.out.println(bean1);

        UserService1 bean2 = applicationContext1.getBean(UserService1.class);
        System.out.println(bean);
        UserService1 bean3 = applicationContext2.getBean(UserService1.class);
        System.out.println(bean1);

//        bean.startTransactional();
    }

}
