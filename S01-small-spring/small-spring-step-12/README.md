# 第十二篇 AOP融合到Bean的生命周期

本篇最重要的就是在createBean中，增加了一个新的BeanPostProcessor类

InstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation
在Bean对象执行初始化方法之前，执行此方法，用于代理aop的实现

在初始化之前，执行此方法，完成了bean的创建，则不会进行BeanPostProcessor#postProcessBeforeInitialization

InstantiationAwareBeanPostProcessor实现类为DefaultAdvisorAutoProxyCreator，在内部
```java
    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        if (isInfrastructureClass(beanClass)) return null;

        Collection<AspectJExpressionPointcutAdvisor> advisors = beanFactory.getBeansOfType(AspectJExpressionPointcutAdvisor.class).values();

        for (AspectJExpressionPointcutAdvisor advisor : advisors) {
            ClassFilter classFilter = advisor.getPointcut().getClassFilter();
            if (!classFilter.matches(beanClass)) continue;

            AdvisedSupport advisedSupport = new AdvisedSupport();

            TargetSource targetSource = null;

            try {
                targetSource = new TargetSource(beanClass.getDeclaredConstructor().newInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }

            advisedSupport.setTargetSource(targetSource);
            advisedSupport.setMethodInterceptor((MethodInterceptor) advisor.getAdvice());
            advisedSupport.setMethodMatcher(advisor.getPointcut().getMethodMatcher());
            advisedSupport.setProxyTargetClass(false);

            return new ProxyFactory(advisedSupport).getProxy();
        }
        return null;
    }
```
完成了对类型的匹配和advisedSupport的装配，如果需要创建的bean被AspectJExpressionPointcutAdvisor
所拦截，则创建aop的代理类，否则返回null