package org.springframework.aop;


import org.aopalliance.intercept.MethodInterceptor;

public class AdvisedSupport {

    private boolean proxyTargetClass = false;
    //被代理的目标对象
    private TargetSource targetSource;
    //方法拦截器
    private MethodInterceptor methodInterceptor;
    //方法匹配起（检查目标方法是否符合通知条件）
    private MethodMatcher methodMatcher;

    public TargetSource getTargetSource() {
        return targetSource;
    }

    public AdvisedSupport setTargetSource(TargetSource targetSource) {
        this.targetSource = targetSource;
        return this;
    }

    public boolean isProxyTargetClass() {
        return proxyTargetClass;
    }

    public AdvisedSupport setProxyTargetClass(boolean proxyTargetClass) {
        this.proxyTargetClass = proxyTargetClass;
        return this;
    }

    public MethodInterceptor getMethodInterceptor() {
        return methodInterceptor;
    }

    public AdvisedSupport setMethodInterceptor(MethodInterceptor methodInterceptor) {
        this.methodInterceptor = methodInterceptor;
        return this;
    }

    public MethodMatcher getMethodMatcher() {
        return methodMatcher;
    }

    public AdvisedSupport setMethodMatcher(MethodMatcher methodMatcher) {
        this.methodMatcher = methodMatcher;
        return this;
    }
}
