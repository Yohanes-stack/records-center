package test.srpc.registry.loadbalancer;

public enum LoadBalancerType {

    CONSISTENT_HASH,

    RoundRobin;

    public static LoadBalancerType toLoadBalancer(String loadBalancer){
        for(LoadBalancerType value : values()){
            if(value.toString().equals(loadBalancer)){
                return value;
            }
        }
        return null;
    }
}
