package com.power.gateway.bind;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.dubbo.rpc.service.GenericService;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * 泛化调用静态代理：方便做一些拦截处理
 * 给HTTP对应的RPC调用，做一层代理控制
 * 每调用到一个 http 对应的网关方法，就会代理的方式调用到 RPC 对应的泛化调用方法上
 */
public class GenericReferenceProxy implements MethodInterceptor {

    private final GenericService genericService;

    private final String methodName;

    public GenericReferenceProxy(GenericService genericService, String methodName) {
        this.genericService = genericService;
        this.methodName = methodName;
    }

    /**
     * 做一层代理控制，后续不止是可以使用Dubbo泛化调用，也可以是其他服务的泛化调用
     * https://dubbo.apache.org/zh/docsv2.7/user/examples/generic-reference/
     */
    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        Parameter[] parameterTypes = method.getParameters();
        String[] parameters = new String[parameterTypes.length];
        for (int i = 0; i < parameters.length; i++) {
            parameters[i] = parameterTypes[0].getName();
        }
        return genericService.$invoke(methodName,parameters,args);
    }
}
