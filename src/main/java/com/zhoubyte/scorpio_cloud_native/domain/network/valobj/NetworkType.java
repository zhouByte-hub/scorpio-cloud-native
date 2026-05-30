package com.zhoubyte.scorpio_cloud_native.domain.network.valobj;

import lombok.Getter;

/**
 * 网络类型枚举，统一 Docker Network Driver 和 K8s Service Type
 */
@Getter
public enum NetworkType {

    BRIDGE("bridge"),
    HOST("host"),
    OVERLAY("overlay"),
    CLUSTER_IP("clusterIp"),
    NODE_PORT("nodePort"),
    LOAD_BALANCER("loadBalancer");

    private final String value;

    NetworkType(String value) {
        this.value = value;
    }

}