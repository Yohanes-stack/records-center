package org.springframework.beans.factory;

/**
 * 实现此接口的Bean对象，会在BeanFactory设置属性后做出相应的处理
 * 如：执行自定义初始化，或者仅仅检查是否设置了所有强制属性
 */
public interface InitializingBean {

    /**
     *
     * @throws Exception
     */
    void afterPropertiesSet() throws Exception;
}
