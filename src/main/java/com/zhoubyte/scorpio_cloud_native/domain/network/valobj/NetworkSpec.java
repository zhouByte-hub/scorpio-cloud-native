package com.zhoubyte.scorpio_cloud_native.domain.network.valobj;

import lombok.Builder;
import lombok.Value;

/**
 * 网络规格值对象，统一承载 Docker Network 配置和 K8s Service Spec
 */
@Value
@Builder
public class NetworkSpec {

    String subnet;
    String gateway;
    String driver;
    boolean internal;
    boolean enableIPv6;

}