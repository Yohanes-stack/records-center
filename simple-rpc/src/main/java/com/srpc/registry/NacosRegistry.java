package com.srpc.registry;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.naming.pojo.Service;
import com.alibaba.nacos.common.utils.CollectionUtils;
import com.srpc.common.ServiceMeta;
import com.srpc.registry.loadbalancer.LoadBalancerType;

import java.io.IOException;
import java.util.*;

public class NacosRegistry extends AbstractRegistryService {

    private final NamingService namingService;

    private boolean enableCache = true;

    Map<String, ServiceMeta> serviceMetas = new HashMap<>();

    private String currentServiceName = buildServiceKey(rpcProperties.getServiceName(), rpcProperties.getVersion());


    public NacosRegistry() throws NacosException {

        // create nacos client properties.
        Properties properties = new Properties();
        properties.setProperty(PropertyKeyConst.SERVER_ADDR, rpcProperties.getRegisterAddr());
        //enable cache
        properties.setProperty(PropertyKeyConst.NAMING_LOAD_CACHE_AT_START, String.valueOf(enableCache));

        this.namingService = NamingFactory.createNamingService(properties);
    }


    @Override
    public void register(ServiceMeta serviceMeta) throws Exception {
        namingService.registerInstance(rpcProperties.getServiceName(), rpcProperties.getIp(), rpcProperties.getProtocolPort());

        Instance instance = new Instance();
        instance.setIp(rpcProperties.getIp());
        instance.setPort(rpcProperties.getProtocolPort());

        Map<String, String> metadata = new HashMap<>();
        //get services.
        List<ServiceMeta> discoveries = discoveries(rpcProperties.getServiceName());
        for (ServiceMeta discovery : discoveries) {
            metadata.put(discovery.getServiceName(), JSON.toJSONString(discovery));
        }
        instance.setMetadata(metadata);
//        instance.setMetadata(serviceMetas);
        //TODO waiting for the next version to updating
        instance.setWeight(1.0);

        namingService.registerInstance("test", instance);


    }

    @Override
    public void unRegister(ServiceMeta serviceMeta) throws Exception {

    }

    @Override
    public ServiceMeta discovery(String serviceName, int invokerHashCode, LoadBalancerType loadBalancerType) throws Exception {
        return null;
    }

    @Override
    public List<ServiceMeta> discoveries(String serviceName) {
        List<ServiceMeta> services = new ArrayList<>();
        List<Instance> allInstances = null;
        try {
            allInstances = namingService.getAllInstances(serviceName);
        } catch (NacosException e) {
            // log
            throw new RuntimeException(e);
        }

        if (CollectionUtils.isNotEmpty(allInstances)) {
            Instance instance = allInstances.get(0);
            Map<String, String> metadata = instance.getMetadata();
            metadata.forEach((k, v) -> services.add(JSON.parseObject(v, ServiceMeta.class)));
        }

        return services;
    }

    @Override
    public void destroy() throws IOException {
        List<Instance> allInstances = null;
        try {
            allInstances = namingService.getAllInstances(currentServiceName);
            for (Instance instance : allInstances) {
                namingService.deregisterInstance(currentServiceName, instance);
            }
        } catch (NacosException e) {
            // log
            throw new RuntimeException(e);
        }


    }
}
