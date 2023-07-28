package org.springframework.test.bean;

import org.junit.Test;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public class ProxyBeanFactory implements FactoryBean<TestDao> {
    @Override
    public TestDao getObject() throws Exception {
        InvocationHandler handle = (proxy, method, args) -> {

            //添加排除方法
            if ("toString".equals(method.getName())) return this.toString();

            Map<String, String> hashMap = new HashMap<>();
            hashMap.put("10001", "张三");
            hashMap.put("10002", "李四");
            hashMap.put("10003", "阿毛");
            return "你被代理了" + method.getName() + ":" + hashMap.get(args[0].toString());
        };
        return (TestDao) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{TestDao.class}, handle);
    }

    @Override
    public Class<?> getObjectType() {
        return TestDao.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
