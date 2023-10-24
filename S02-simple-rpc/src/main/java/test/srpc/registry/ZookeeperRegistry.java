package test.srpc.registry;

import test.srpc.common.ServiceMeta;
import test.srpc.registry.loadbalancer.LoadBalancerFactory;
import test.srpc.registry.loadbalancer.LoadBalancerType;
import test.srpc.registry.loadbalancer.ServiceLoadBalancer;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ZookeeperRegistry extends AbstractRegistryService {

    /**
     * Duration of waiting for retry after connection failure
     */
    public static final int BASE_SLEEP_TIME_MS = 1000;
    /**
     * Maximum number of retries
     */
    public static final int MAX_RETRIES = 3;
    /**
     * root path
     */
    public static final String ZK_BASE_PATH = "/simple_rpc";

    private final ServiceDiscovery<ServiceMeta> serviceDiscovery;

    /**
     * start zookeeper
     *
     * @throws Exception
     */
    public ZookeeperRegistry() throws Exception {
        String registerAddr = rpcProperties.getRegisterAddr();
        CuratorFramework client = CuratorFrameworkFactory.newClient(registerAddr, new ExponentialBackoffRetry(BASE_SLEEP_TIME_MS, MAX_RETRIES));
        client.start();
        JsonInstanceSerializer<ServiceMeta> serializer = new JsonInstanceSerializer<>(ServiceMeta.class);
        this.serviceDiscovery = ServiceDiscoveryBuilder.builder(ServiceMeta.class)
                .client(client)
                .serializer(serializer)
                .basePath(ZK_BASE_PATH)
                .build();
        this.serviceDiscovery.start();
    }

    @Override
    public void register(ServiceMeta serviceMeta) throws Exception {
        ServiceInstance<ServiceMeta> serviceInstance = ServiceInstance.<ServiceMeta>builder()
                .name(buildServiceKey(serviceMeta.getServiceName(), serviceMeta.getServiceVersion()))
                .address(serviceMeta.getServiceAddr())
                .port(serviceMeta.getServicePort())
                .payload(serviceMeta)
                .build();
        serviceDiscovery.registerService(serviceInstance);
    }

    @Override
    public void unRegister(ServiceMeta serviceMeta) throws Exception {
        ServiceInstance<ServiceMeta> serviceInstance = ServiceInstance
                .<ServiceMeta>builder()
                .name(serviceMeta.getServiceName())
                .address(serviceMeta.getServiceAddr())
                .port(serviceMeta.getServicePort())
                .payload(serviceMeta)
                .build();
        serviceDiscovery.unregisterService(serviceInstance);
    }

    @Override
    public ServiceMeta discovery(String serviceName, int invokerHashCode, LoadBalancerType loadBalancerType) throws Exception {
        List<ServiceMeta> serviceMetas = listServices(serviceName);
        ServiceLoadBalancer loadBalancer = LoadBalancerFactory.getInstance(loadBalancerType);
        ServiceMeta selectService = loadBalancer.select(serviceMetas, invokerHashCode);
        return selectService;
    }

    private List<ServiceMeta> listServices(String serviceName) throws Exception {
        Collection<ServiceInstance<ServiceMeta>> serviceInstances = serviceDiscovery.queryForInstances(serviceName);
        List<ServiceMeta> serviceMetas = serviceInstances.stream().map(serviceMetaServiceInstance -> serviceMetaServiceInstance.getPayload()).collect(Collectors.toList());
        return serviceMetas;
    }

    @Override
    public List<ServiceMeta> discoveries(String serviceName) {
        try {
            return listServices(serviceName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void destroy() throws IOException {
        serviceDiscovery.close();
    }
}
