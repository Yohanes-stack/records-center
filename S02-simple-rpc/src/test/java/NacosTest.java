import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.junit.Test;
import org.springframework.util.CollectionUtils;

import java.util.*;


public class NacosTest {


    @Test
    public void register() throws Exception {
        NamingService namingService = NamingFactory.createNamingService("127.0.0.1:8848");

        List<Instance> instances = new ArrayList<>();

        // 创建三个实例，并设置不同的名称、IP地址和元数据
        Instance instance1 = new Instance();
        instance1.setIp("127.0.0.1");
        instance1.setPort(8080);
        instance1.setMetadata(Collections.singletonMap("timeout", "1000"));
        instances.add(instance1);

        Instance instance2 = new Instance();
        instance2.setIp("127.0.0.2");
        instance2.setPort(8080);
        instance2.setMetadata(Collections.singletonMap("timeout", "2000"));
        instances.add(instance2);

        Instance instance3 = new Instance();
        instance3.setIp("127.0.0.3");
        instance3.setPort(8080);
        instance3.setMetadata(Collections.singletonMap("timeout", "3000"));
        instances.add(instance3);

        // 注册所有实例
        namingService.registerInstance("test", instance1);
        namingService.registerInstance("test", instance2);
        namingService.registerInstance("test", instance3);

    }

    @Test
    public void register1() throws Exception {
        NamingService namingService = NamingFactory.createNamingService("127.0.0.1:8848");


        // 创建三个实例，并设置不同的名称、IP地址和元数据
        for (int i = 0; i < 3; i++) {
            Instance instance3 = new Instance();
            instance3.setServiceName("test" + i);
            instance3.setIp("127.0.0.3");
            instance3.setPort(8080);
            instance3.setMetadata(Collections.singletonMap("timeout", "3000" + i));
            namingService.registerInstance("test", instance3);
        }
    }

    @Test
    public void register2() throws Exception {
        NamingService namingService = NamingFactory.createNamingService("127.0.0.1:8848");

        Instance instance = new Instance();
        instance.setIp("127.0.0.1");
        instance.setPort(8080);

        Map<String, String> metadata = new HashMap<>();
        metadata.put("rpc1", "timeout=1000");
        metadata.put("rpc2", "timeout=2000");
        metadata.put("rpc3", "timeout=3000");
        metadata.put("rpc4", "timeout=4000");
        metadata.put("rpc5", "timeout=5000");

        instance.setMetadata(metadata);

        namingService.registerInstance("test", instance);
    }

    @Test
    public void getMetadataValue() throws Exception {
        NamingService namingService = NamingFactory.createNamingService("127.0.0.1:8848");

        List<Instance> instance = namingService.getAllInstances("test");
        if (!CollectionUtils.isEmpty(instance)) {
            Instance instance1 = instance.get(0);
            Map<String, String> metadata = instance1.getMetadata();
            Collection<String> values = metadata.values();
            for (String value : values) {
                System.out.println(value);
            }
        }

    }


}
