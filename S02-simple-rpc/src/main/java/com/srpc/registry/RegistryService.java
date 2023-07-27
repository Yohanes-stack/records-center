package com.srpc.registry;

import com.srpc.common.ServiceMeta;
import com.srpc.registry.loadbalancer.LoadBalancerType;

import java.io.IOException;
import java.util.List;

/**
 * 注册中心接口
 */
public interface RegistryService {
    /**
     * 服务注册
     *
     * @param serviceMeta
     * @throws Exception
     */
    void register(ServiceMeta serviceMeta) throws Exception;

    /**
     * 服务下线
     *
     * @param serviceMeta
     * @throws Exception
     */
    void unRegister(ServiceMeta serviceMeta) throws Exception;

    /**
     * 查询服务
     *
     * @param serviceName
     * @param invokerHashCode
     * @param loadBalancerType
     * @return
     * @throws Exception
     */
    ServiceMeta discovery(String serviceName, int invokerHashCode, LoadBalancerType loadBalancerType) throws Exception;

    /**
     * 获取 serviceName 下的所有服务
     *
     * @param serviceName
     * @return
     */
    List<ServiceMeta> discoveries(String serviceName);

    /**
     * 关闭
     *
     * @throws IOException
     */
    void destroy() throws IOException;
}
