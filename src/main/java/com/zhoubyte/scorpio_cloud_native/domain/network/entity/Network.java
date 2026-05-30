package com.zhoubyte.scorpio_cloud_native.domain.network.entity;

import com.zhoubyte.scorpio_cloud_native.domain.network.valobj.NetworkSpec;
import com.zhoubyte.scorpio_cloud_native.domain.network.valobj.NetworkType;
import com.zhoubyte.scorpio_cloud_native.domain.network.valobj.ServicePort;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * 网络聚合根，统一承载 Docker Network 和 K8s Service
 */
@Getter
@Builder
public class Network {

    /** 网络唯一标识，Docker 为 Network ID，K8s 为 Service UID */
    String id;

    /** 网络名称 */
    String name;

    /** K8s 命名空间，Docker 场景下可为空 */
    String namespace;

    /** 网络类型：BRIDGE/HOST/OVERLAY/CLUSTER_IP/NODE_PORT/LOAD_BALANCER */
    NetworkType networkType;

    /** 网络规格配置 */
    NetworkSpec spec;

    /** 服务端口列表，K8s Service 的端口配置 */
    List<ServicePort> ports;

    /** 标签，用于资源分类 */
    Map<String, String> labels;

    /** 选择器，K8s Service 用于匹配后端 Pod */
    Map<String, String> selectors;

    /** 创建时间 */
    Instant createdAt;

}