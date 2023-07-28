# 第八篇 标记接口Aware接口，感知容器对象

Aware标记接口的作用就是用来获容器的内容


BeanFactoryAware 获取当前容器的BeanFactory

BeanNameAware 获取当前类的BeanName

ApplicationContextAware 获取当前容器上下文

BeanClassLoaderAware 获取当前容器类加载器

主要逻辑都放在initializeBean进行操作   
```    
private Object initializeBean(String beanName, Object bean, BeanDefinition beanDefinition) {

        //invokeAwareMethods
        if(bean instanceof Aware){
            if(bean instanceof BeanFactoryAware){
                ((BeanFactoryAware) bean).setBeanFactory(this);
            }
            if(bean instanceof BeanClassLoaderAware){
                ((BeanClassLoaderAware) bean).setBeanClassLoader(getBeanClassLoader());
            }
            if(bean instanceof BeanNameAware){
                ((BeanNameAware) bean).setBeanName(beanName);
            }
        }

        //1. 执行BeanPostProcessor Before处理
        Object wrappedBean = applyBeanPostProcessorsBeforeInitialization(bean, beanName);

        //2.执行bean对象的初始化方法
        try {
            invokeInitMethods(beanName, wrappedBean, beanDefinition);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //3.执行BeanPostProcessor After处理
        wrappedBean = applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
        return wrappedBean;
    }
```
ApplicationContextAware则是通过beanPostProcessor的前置处理来进行context的设置

