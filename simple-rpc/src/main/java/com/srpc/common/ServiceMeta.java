package com.srpc.common;

import java.io.Serializable;

/**
 * 服务元数据
 */
public class ServiceMeta implements Serializable {

    private String serviceName;

    private String serviceVersion;

    private String serviceAddr;

    private int servicePort;

    private long endTime;

    private String UUID;


}
