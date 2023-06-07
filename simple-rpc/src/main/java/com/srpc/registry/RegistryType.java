package com.srpc.registry;

public enum RegistryType {
    NACOS,
    ZOOKEEPER,
    REDIS;
    public static RegistryType toRegistry(String registerType) {
        for (RegistryType value : values()) {
            if (value.toString().equals(registerType)) {
                return value;
            }
        }
        return null;
    }
}
