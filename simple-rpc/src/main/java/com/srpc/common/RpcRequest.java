package com.srpc.common;

import java.io.Serializable;

public class RpcRequest implements Serializable {

    private String serviceVersion;

    private String className;

    private String methodName;

    private Object[] params;

    private Class<?>[] parameterTypes;
}
