package org.springframework.test;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class TestService implements InitializingBean, DisposableBean {
    @Override
    public void destroy() throws Exception {
        System.out.println("执行destroy");
    }

    public void destroyDataMethod(){
        System.out.println("执行：destroy-method");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("执行afterPropertiesSet");
    }
}
