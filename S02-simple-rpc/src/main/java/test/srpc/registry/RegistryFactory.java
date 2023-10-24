package test.srpc.registry;

/**
 * @description: 注册工厂
 * @Author: Xhy
 * @gitee: https://gitee.com/XhyQAQ
 * @copyright: B站: https://space.bilibili.com/152686439
 * @CreateTime: 2023-04-24 23:39
 */
public class RegistryFactory {

    private static volatile RegistryService registryService;


    public static RegistryService getInstance(RegistryType type) throws Exception {

        if (null == registryService) {
            synchronized (RegistryFactory.class) {
                if (null == registryService) {
                    switch (type) {
                        case ZOOKEEPER:
                            registryService = new ZookeeperRegistry();
                            break;
                        case REDIS:
                            registryService = new RedisRegistry();
                    }
                }
            }
        }
        return registryService;
    }

}
