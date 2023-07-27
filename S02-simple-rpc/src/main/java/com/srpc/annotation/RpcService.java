package com.srpc.annotation;

import com.srpc.registry.loadbalancer.LoadBalancerType;
import com.srpc.tolerant.FaultTolerantType;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
public @interface RpcService {
    String serviceVersion() default "1.0";
}
