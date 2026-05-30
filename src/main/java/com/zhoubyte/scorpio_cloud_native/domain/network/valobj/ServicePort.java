package com.zhoubyte.scorpio_cloud_native.domain.network.valobj;

import lombok.Value;

/**
 * 服务端口值对象，统一承载 Docker 端口映射和 K8s ServicePort
 */
@Value
public class ServicePort {

    String name;
    int port;
    int targetPort;
    String protocol;
    Integer nodePort;

    public static ServicePort of(String name, int port, int targetPort, String protocol) {
        return new ServicePort(name, port, targetPort, protocol, null);
    }

    public static ServicePort nodePort(String name, int port, int targetPort, int nodePort) {
        return new ServicePort(name, port, targetPort, "TCP", nodePort);
    }

    public boolean hasNodePort() {
        return nodePort != null && nodePort > 0;
    }

}