package org.springframework.beans.factory;

import org.springframework.beans.BeansException;

import java.util.Map;

/**
 * 扩展Bean工厂接口
 */
public interface ListableBeanFactory extends BeanFactory {
    /**
     * 按照类型返回Bean实例
     * @param type
     * @return
     * @param <T>
     * @throws BeansException
     */
    <T> Map<String,T> getBeansOfType(Class<T> type) throws BeansException;

    /**
     *
     * @return  返回注册表中所有的Bean对象
     */
    String[] getBeanDefinitionNames();

}
