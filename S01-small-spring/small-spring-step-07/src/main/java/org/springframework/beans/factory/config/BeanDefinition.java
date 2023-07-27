package org.springframework.beans.factory.config;

import org.springframework.beans.PropertyValues;

public class BeanDefinition {
    private Class beanClass;

    private PropertyValues propertyValues;

    private String initMethodName;

    private String destroyMethodName;



    public BeanDefinition(Class beanClass) {
        this.beanClass = beanClass;
        this.propertyValues = new PropertyValues();
    }

    public BeanDefinition(Class beanClass, PropertyValues propertyValues) {
        this.beanClass = beanClass;
        this.propertyValues = propertyValues;
    }

    public String getInitMethodName() {
        return initMethodName;
    }

    public BeanDefinition setInitMethodName(String initMethodName) {
        this.initMethodName = initMethodName;
        return this;
    }

    public String getDestroyMethodName() {
        return destroyMethodName;
    }

    public BeanDefinition setDestroyMethodName(String destroyMethodName) {
        this.destroyMethodName = destroyMethodName;
        return this;
    }

    public Class getBeanClass() {
        return beanClass;
    }

    public void setBeanClass(Class beanClass) {
        this.beanClass = beanClass;
    }

    public PropertyValues getPropertyValues() {
        return propertyValues;
    }

    public BeanDefinition setPropertyValues(PropertyValues propertyValues) {
        this.propertyValues = propertyValues;
        return this;
    }
}
