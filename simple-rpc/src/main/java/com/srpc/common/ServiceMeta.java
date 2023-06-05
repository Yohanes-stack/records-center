package com.srpc.common;

import java.io.Serializable;
import java.util.Objects;

/**
 * basic service information.
 */
public class ServiceMeta implements Serializable {

    private String serviceName;

    private String serviceVersion;

    private String serviceAddr;

    private int servicePort;

    private long endTime;

    private String identifying;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceVersion() {
        return serviceVersion;
    }

    public void setServiceVersion(String serviceVersion) {
        this.serviceVersion = serviceVersion;
    }

    public String getServiceAddr() {
        return serviceAddr;
    }

    public void setServiceAddr(String serviceAddr) {
        this.serviceAddr = serviceAddr;
    }

    public int getServicePort() {
        return servicePort;
    }

    public void setServicePort(int servicePort) {
        this.servicePort = servicePort;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getIdentifying() {
        return identifying;
    }

    public void setIdentifying(String identifying) {
        this.identifying = identifying;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceMeta that = (ServiceMeta) o;
        return servicePort == that.servicePort
                && Objects.equals(serviceName, that.serviceName)
                && Objects.equals(serviceVersion, that.serviceVersion)
                && Objects.equals(serviceAddr, that.serviceAddr)
                && Objects.equals(identifying, that.identifying);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceName, serviceVersion, serviceAddr, servicePort, identifying);
    }
}
