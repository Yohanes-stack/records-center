package test.srpc.annotation;

import test.srpc.registry.loadbalancer.LoadBalancerType;
import test.srpc.tolerant.FaultTolerantType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RpcReference {

    String serviceVersion() default "1.0";

    long timeout() default 5000;

    LoadBalancerType loadBalancerType() default LoadBalancerType.RoundRobin;

    FaultTolerantType faultTolerantType() default FaultTolerantType.Failover;

    long retryCount() default 3;
}
