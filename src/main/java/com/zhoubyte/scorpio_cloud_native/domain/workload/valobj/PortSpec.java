package com.zhoubyte.scorpio_cloud_native.domain.workload.valobj;

import lombok.Value;

/**
 * 端口规格值对象，统一承载 Docker 端口映射和 K8s ServicePort
 */
@Value
public class PortSpec {

    /** 端口名称，K8s ServicePort 必填 */
    String name;

    /** 容器内部端口 */
    int containerPort;

    /** 主机端口，Docker 端口映射或 K8s hostPort */
    Integer hostPort;

    /** 协议：TCP/UDP */
    String protocol;

    public static PortSpec of(String name, int containerPort, Integer hostPort, String protocol) {
        return new PortSpec(name, containerPort, hostPort, protocol);
    }

    public static PortSpec of(int containerPort, Integer hostPort) {
        return new PortSpec(null, containerPort, hostPort, "TCP");
    }

    public boolean isExposed() {
        return hostPort != null && hostPort > 0;
    }

}