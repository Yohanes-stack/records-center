package test.srpc.common;

/**
 * build rpc service key
 *
 * @author yohanes
 */
public class RpcServiceNameBuilder {
    public static String buildServiceKey(String serviceName, String serviceVersion) {
        return String.join("$", serviceName, serviceVersion);
    }
}
