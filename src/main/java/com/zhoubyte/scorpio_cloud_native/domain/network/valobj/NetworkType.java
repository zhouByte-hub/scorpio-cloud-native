package com.zhoubyte.scorpio_cloud_native.domain.network.valobj;

import lombok.Getter;

/**
 * 网络类型枚举，统一 Docker Network Driver 和 K8s Service Type
 */
@Getter
public enum NetworkType {

    /** Docker Bridge 网络，默认网络类型 */
    BRIDGE("bridge"),

    /** Docker/K8s Host 网络，共享宿主机网络栈 */
    HOST("host"),

    /** Docker Overlay 网络，跨主机通信 */
    OVERLAY("overlay"),

    /** K8s ClusterIP Service，集群内部访问 */
    CLUSTER_IP("clusterIp"),

    /** K8s NodePort Service，通过宿主机端口暴露 */
    NODE_PORT("nodePort"),

    /** K8s LoadBalancer Service，通过云厂商负载均衡器暴露 */
    LOAD_BALANCER("loadBalancer");

    private final String value;

    NetworkType(String value) {
        this.value = value;
    }

}