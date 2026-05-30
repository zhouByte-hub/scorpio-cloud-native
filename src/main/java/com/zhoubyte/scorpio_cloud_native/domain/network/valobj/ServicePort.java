package com.zhoubyte.scorpio_cloud_native.domain.network.valobj;

import lombok.Value;

/**
 * 服务端口值对象，统一承载 Docker 端口映射和 K8s ServicePort
 */
@Value
public class ServicePort {

    /** 端口名称，K8s ServicePort 必填 */
    String name;

    /** Service 暴露端口，K8s Service.port */
    int port;

    /** 容器目标端口，K8s Service.targetPort */
    int targetPort;

    /** 协议：TCP/UDP */
    String protocol;

    /** NodePort 端口号，仅 NodePort 类型有效 */
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