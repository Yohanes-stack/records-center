package test.srpc.consumer;

import test.srpc.annotation.RpcReference;
import test.srpc.configuration.RpcProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ConsumerPostProcessor implements ApplicationContextAware, BeanClassLoaderAware, BeanFactoryPostProcessor, EnvironmentAware {

    private Logger log = LoggerFactory.getLogger(ConsumerPostProcessor.class);
    private ApplicationContext context;

    private ClassLoader classLoader;

    private Environment environment;

    private final Map<String, BeanDefinition> rpcRefBeanDefinitions = new LinkedHashMap<>();
    private RpcProperties rpcProperties;

    public void setRpcProperties(RpcProperties rpcProperties) {
        this.rpcProperties = rpcProperties;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        for (String beanDefinitionName : beanFactory.getBeanDefinitionNames()) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanDefinitionName);
            String beanClassName = beanDefinition.getBeanClassName();
            if (beanClassName != null) {
                Class<?> aClass = ClassUtils.resolveClassName(beanClassName, this.classLoader);
                ReflectionUtils.doWithFields(aClass, this::parseRpcReference);
            }
        }
        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
        this.rpcRefBeanDefinitions.forEach((beanName, beanDefinition) -> {
            if (context.containsBean(beanName)) {
                throw new IllegalArgumentException("spring context already has a bean named " + beanName);
            }
            registry.registerBeanDefinition(beanName,rpcRefBeanDefinitions.get(beanName));
            if(log.isDebugEnabled()){
                log.debug("register rpcReferenceBean {} succeed",beanName);
            }
        });
    }

    public void parseRpcReference(Field field) {
        RpcReference annotation = AnnotationUtils.getAnnotation(field, RpcReference.class);
        if (annotation != null) {
            String address = rpcProperties.getRegisterAddr();
            String type = rpcProperties.getRegisterType();
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(RpcReferenceBean.class);
            //执行init方法
            builder.setInitMethodName("init");
            builder.addPropertyValue("interfaceClass", field.getType());
            builder.addPropertyValue("serviceVersion", annotation);
            builder.addPropertyValue("registryType", type);
            builder.addPropertyValue("registryAddr", address);
            builder.addPropertyValue("timeout", annotation.timeout());
            builder.addPropertyValue("loadBalancerType", annotation.loadBalancerType());
            builder.addPropertyValue("faultTolerantType", annotation.faultTolerantType());
            builder.addPropertyValue("retryCount", annotation.retryCount());
            AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
            rpcRefBeanDefinitions.put(field.getName(), beanDefinition);

        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
