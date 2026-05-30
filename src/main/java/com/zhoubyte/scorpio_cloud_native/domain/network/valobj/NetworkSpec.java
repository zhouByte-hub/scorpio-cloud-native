package com.zhoubyte.scorpio_cloud_native.domain.network.valobj;

import lombok.Builder;
import lombok.Value;

/**
 * 网络规格值对象，统一承载 Docker Network 配置和 K8s Service Spec
 */
@Value
@Builder
public class NetworkSpec {

    /** 子网地址，如 172.17.0.0/16 */
    String subnet;

    /** 网关地址，如 172.17.0.1 */
    String gateway;

    /** 网络驱动，Docker 为 bridge/overlay/macvlan，K8s 无此概念 */
    String driver;

    /** 是否为内部网络，不允许外部访问 */
    boolean internal;

    /** 是否启用 IPv6 */
    boolean enableIPv6;

}