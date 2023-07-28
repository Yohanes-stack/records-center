# 第九篇 Bean对象作用域 singleton 和 prototype

在BeanDefinition增加Scope字段，然后在xml解析时解析出作用域
```
            String beanScope = bean.getAttribute("scope");
            
            if (StrUtil.isNotEmpty(beanScope)) {
                beanDefinition.setScope(beanScope);
            }
```


然后在AutoWireCapableBeanFactory中的createBean中增加singleton的判断，如果非单例则不放入单例池
此时如果是非单例，在这里不进入单例池，只通过FactoryBean中的getObject进行对象创建
```

    @Override
    protected Object createBean(String beanName, BeanDefinition beanDefinition, Object[] args) throws BeansException {
        Object bean = null;
        try {
            bean = createBeanInstance(beanName, beanDefinition, args);
            //给Bean填充属性
            applyPropertyValues(beanName, bean, beanDefinition);
            //执行Bean的初始化方法和BeanPostProcessor的前置和后置处理器
            bean = initializeBean(beanName, bean, beanDefinition);
        } catch (Exception e) {
            throw new BeansException("Instantiation of bean failed", e);
        }
        //注册实现了 DisposableBean 接口的Bean对象
        registerDisposableBeanIfNecessary(beanName, bean, beanDefinition);

        //判断SCOPE_SINGLETON 、SCOPE_PROTOTYPE
        if (beanDefinition.isSingleton()) {
            addSingleton(beanName, bean);
        }

        return bean;
    }
```

```
public interface FactoryBean<T> {

    T getObject() throws Exception;

    Class<?> getObjectType();

    boolean isSingleton();

}

```