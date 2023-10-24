package test.srpc.tolerant;

import java.util.HashMap;
import java.util.Map;

/**
 * 集群容错工厂
 */
public class FaultTolerantFactory {

    private static Map<FaultTolerantType,FaultTolerantStrategy> faultTolerantStrategyMap = new HashMap<>();
    static {

    }

    public static FaultTolerantStrategy get(FaultTolerantType faultTolerantType){
        return faultTolerantStrategyMap.get(faultTolerantType);
    }
}
