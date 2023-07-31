package org.springframework.test;

import org.aopalliance.intercept.MethodInterceptor;
import org.junit.Test;
import org.springframework.aop.AdvisedSupport;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.TargetSource;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.framework.Cglib2AopProxy;
import org.springframework.aop.framework.JDKDynamicAopProxy;
import org.springframework.aop.framework.ReflectiveMethodInvocation;
import org.springframework.test.bean.IUserService;
import org.springframework.test.bean.UserService;
import org.springframework.test.bean.UserServiceInterceptor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ApiTest {

    @Test
    public void test_aop() throws NoSuchMethodException {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut("execution(* org.springframework.test.bean.UserService.*(..))");
        Class<UserService> clazz = UserService.class;
        Method method = clazz.getDeclaredMethod("queryUserInfo");

        System.out.println(pointcut.matches(clazz));
        System.out.println(pointcut.matches(method, clazz));

    }

    @Test
    public void test_dynamic() {
        //目标对象
        IUserService userService = new UserService();
        //组装代理信息
        AdvisedSupport advisedSupport = new AdvisedSupport();
        advisedSupport.setTargetSource(new TargetSource(userService));
        advisedSupport.setMethodInterceptor(new UserServiceInterceptor());
        advisedSupport.setMethodMatcher(new AspectJExpressionPointcut("execution(* org.springframework.test.bean.IUserService.*(..))"));

        //代理对象(JdkDynamicAopProxy)
        IUserService proxy_jdk = (IUserService) new JDKDynamicAopProxy(advisedSupport).getProxy();
        System.out.println(proxy_jdk.queryUserInfo());
        //代理对象(Cglib2AopProxy)
        Object proxy_cglib = new Cglib2AopProxy(advisedSupport).getProxy();
        System.out.println(proxy_jdk.register("花花世界迷人眼"));
    }

    @Test
    public void test_proxy_class() {
        IUserService userService = (IUserService) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{IUserService.class}, ((proxy, method, args) -> "你被代理了"));
        String result = userService.queryUserInfo();
        System.out.println(result);
    }
    @Test
    public void test_proxy_method() {
        //目标对象(可以替换成任何的目标对象)
        UserService targetObj = new UserService();
        //AOP代理
        IUserService proxy = (IUserService) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), targetObj.getClass().getInterfaces(), new InvocationHandler() {

            //方法匹配器
            MethodMatcher methodMatcher = new AspectJExpressionPointcut("execution(* org.springframework.test.bean.IUserService.*(..))");

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (methodMatcher.matches(method, targetObj.getClass())) {
                    //方法拦截器
                    MethodInterceptor methodInterceptor = invocation -> {
                        long start = System.currentTimeMillis();
                        try {
                            return invocation.proceed();
                        } finally {
                            System.out.println("监控 - Begin By AOP");
                            System.out.println("方法名称：" + invocation.getMethod().getName());
                            System.out.println("方法耗时：" + (System.currentTimeMillis() - start) + "ms");
                            System.out.println("监控 - End\r\n");
                        }
                    };
                    return methodInterceptor.invoke(new ReflectiveMethodInvocation(targetObj, method, args));
                }
                return method.invoke(targetObj,args);
            }
        });
        String result = proxy.queryUserInfo();
        System.out.println("测试结果:" + result);
    }
}
