package com.srpc.registry;

import com.alibaba.fastjson.JSON;
import com.srpc.common.ServiceMeta;
import com.srpc.registry.loadbalancer.LoadBalancerFactory;
import com.srpc.registry.loadbalancer.LoadBalancerType;
import com.srpc.registry.loadbalancer.ServiceLoadBalancer;
import org.springframework.util.ObjectUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.sql.Array;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 *
 */
public class RedisRegistry extends AbstractRegistryService {

    private JedisPool jedisPool;

    private String identifying;

    private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    /**
     * service information collection
     */
    private Set<String> serviceMap = new HashSet<>();

    private static final int ttl = 10 * 1000;


    public RedisRegistry() {

        String[] split = rpcProperties.getRegisterAddr().split(":");
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        //最大连接数
        poolConfig.setMaxTotal(10);
        //最大空闲数
        poolConfig.setMaxIdle(5);
        jedisPool = new JedisPool(poolConfig, split[0], Integer.valueOf(split[1]));
        this.identifying = UUID.randomUUID().toString();
        // 健康监测
        heartbeat();
    }

    private void heartbeat() {
        int sch = 5;
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            for (String key : serviceMap) {
                //Obtain all service nodes and check if the expiration time
                //of the service node is less than the current time . if less than
                //delete service information.
                List<ServiceMeta> serviceNodes = listServices(key);
                Iterator<ServiceMeta> iterator = serviceNodes.iterator();
                while (iterator.hasNext()) {
                    ServiceMeta node = iterator.next();
                    //remote expiration service
                    if (node.getEndTime() < new Date().getTime()) {
                        iterator.remove();
                    }
                    //2.renewal for 10 seconds
                    if (node.getIdentifying().equals(this.identifying)) {
                        node.setEndTime(node.getEndTime() + ttl / 2);
                    }
                }
                if (!ObjectUtils.isEmpty(serviceNodes)) {
                    this.loadService(key, serviceNodes);
                }
            }
        }, sch, sch, TimeUnit.SECONDS);
    }

    /**
     * ensure the atomicity of Redis when updating services through lua scripts
     *
     * @param key          service key .
     * @param serviceMetas service list .
     */
    @SuppressWarnings("all")
    private void loadService(String key, List<ServiceMeta> serviceMetas) {
        String script = "redis.call('DEL',KEYS[1])\n" +
                "for i=1,#ARGV do\n" +
                "        redis.call('RPUSH',KEYS[i],ARGV[i])\n" +
                "end\n" +
                "redis.call('EXPIRE',KEYS[i],KEYS[2])\n";
        List<String> keys = new ArrayList<>();
        keys.add(key);
        keys.add(String.valueOf(10));
        List<String> values = serviceMetas.stream().map(o -> JSON.toJSONString(o)).collect(Collectors.toList());
        try (Jedis jedis = getJedis()) {
            jedis.eval(script, keys, values);
        }
    }

    private List<ServiceMeta> listServices(String serviceName) {
        try (Jedis jedis = getJedis()) {
            List<String> services = jedis.lrange(serviceName, 0, -1);
            List<ServiceMeta> serviceMetas = services.stream().map(o -> JSON.parseObject(o, ServiceMeta.class)).collect(Collectors.toList());
            return serviceMetas;
        }
    }

    private Jedis getJedis() {
        Jedis jedis = jedisPool.getResource();
        String registerPsw = rpcProperties.getRegisterPsw();
        jedis.auth(registerPsw);
        return jedis;
    }

    /**
     * ensure the atomicity of Redis when updating services through lua scripts
     * registering services in Redis
     *
     * @param serviceMeta serviceMeta
     * @throws Exception
     */
    @Override
    public void register(ServiceMeta serviceMeta) throws Exception {
        String key = buildServiceKey(serviceMeta.getServiceName(), serviceMeta.getServiceVersion());
        if (serviceMap.contains(key)) {
            serviceMap.add(key);
        }
        serviceMeta.setIdentifying(this.identifying);
        serviceMeta.setEndTime(new Date().getTime() + ttl);
        updateRedisService(key, serviceMeta);
    }

    private void updateRedisService(String key, ServiceMeta serviceMeta) {
        try (Jedis jedis = getJedis()) {
            String script = "redis.call('RPUSH', KEYS[1], ARGV[1])\n" +
                    "redis.call('EXPIRE', KEYS[1], ARGV[2])";
            List<String> value = new ArrayList<>();
            value.add(JSON.toJSONString(serviceMeta));
            value.add(String.valueOf(10));
            jedis.eval(script, Collections.singletonList(key), value);
        }
    }

    @Override
    public void unRegister(ServiceMeta serviceMeta) throws Exception {
        String key = buildServiceKey(serviceMeta.getServiceName(), serviceMeta.getServiceVersion());
        if (!serviceMap.contains(key)) {
            return;
        }
        List<ServiceMeta> serviceNodes = listServices(key);

        Iterator<ServiceMeta> iterator = serviceNodes.iterator();
        while (iterator.hasNext()) {
            ServiceMeta service = iterator.next();
            if (service.equals(serviceMeta)) {
                iterator.remove();
            }
        }
        loadService(key, serviceNodes);
    }

    @Override
    public ServiceMeta discovery(String serviceName, int invokerHashCode, LoadBalancerType loadBalancerType) throws Exception {
        ServiceLoadBalancer<ServiceMeta> loadBalancer = LoadBalancerFactory.getInstance(loadBalancerType);
        List<ServiceMeta> serviceMetas = listServices(serviceName);
        return loadBalancer.select(serviceMetas, invokerHashCode);
    }

    @Override
    public List<ServiceMeta> discoveries(String serviceName) {
        return listServices(serviceName);
    }

    @Override
    public void destroy() throws IOException {

    }
}
