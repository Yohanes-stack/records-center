package com.srpc.config;

import com.srpc.consumer.ConsumerPostProcessor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 这样子做的话，会只支持spring boot，想最大程度支持的话，可以在{@link ConsumerPostProcessor} 里面去环境中获取
 */
@ConfigurationProperties
public class RpcProperties {

    /**
     * 端口号
     */
    private int port;

    /**
     * 注册中心地址
     */
    private String registerAddr;

    /**
     * 注册中心类型
     */
    private String registerType;

    /**
     * 注册中心密码
     */
    private String registerPsw;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getRegisterAddr() {
        return registerAddr;
    }

    public void setRegisterAddr(String registerAddr) {
        this.registerAddr = registerAddr;
    }

    public String getRegisterType() {
        return registerType;
    }

    public void setRegisterType(String registerType) {
        this.registerType = registerType;
    }

    public String getRegisterPsw() {
        return registerPsw;
    }

    public void setRegisterPsw(String registerPsw) {
        this.registerPsw = registerPsw;
    }
}
