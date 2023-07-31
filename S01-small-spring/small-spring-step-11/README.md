# 第十一篇 AOP代理
接入AOP，首先引入AOP的相关包
```maven
        <dependency>
            <groupId>aopalliance</groupId>
            <artifactId>aopalliance</artifactId>
            <version>1.0</version>
        </dependency>
        <dependency>
            <groupId>org.aspectj</groupId>
            <artifactId>aspectjweaver</artifactId>
            <version>1.9.7</version>
        </dependency>
```
aspectj就是aop的包，而aopalliance 是aop联盟推出的aop相关标准

aop中，最常用的就是切点表达式，需要匹配类和方法，匹配类使用的是ClassFilter,匹配方法则是MethodMatcher

Pointcut类，则是包含获取这两个类的接口

AspectJExpressionPointcut是Pointcut的实现 简单实现仅支持execution函数。

```java
    @Test
    public void test_aop() throws NoSuchMethodException {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut("execution(* org.springframework.test.bean.UserService.*(..))");
        Class<UserService> clazz = UserService.class;
        Method method = clazz.getDeclaredMethod("queryUserInfo");

        System.out.println(pointcut.matches(clazz));
        System.out.println(pointcut.matches(method, clazz));

    }
```

AdvisedSupport 代理信息配置类
该配置类中包含目标代理类 方法拦截器 以及 方法匹配起
该类将是不同代理实现所需要传入的配置类

JDKDynamicAopProxy JDK代理类

Cglib2AopProxy  Cglib代理类