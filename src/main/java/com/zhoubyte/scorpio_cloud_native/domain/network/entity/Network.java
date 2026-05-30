package com.zhoubyte.scorpio_cloud_native.domain.network.entity;

import com.zhoubyte.scorpio_cloud_native.domain.network.valobj.NetworkSpec;
import com.zhoubyte.scorpio_cloud_native.domain.network.valobj.NetworkType;
import com.zhoubyte.scorpio_cloud_native.domain.network.valobj.ServicePort;
import com.zhoubyte.scorpio_cloud_native.domain.platform.valobj.PlatformType;
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

    String id;
    String name;
    String namespace;
    PlatformType platformType;
    NetworkType networkType;
    NetworkSpec spec;
    List<ServicePort> ports;
    Map<String, String> labels;
    Map<String, String> selectors;
    Instant createdAt;

    public boolean isDockerNetwork() {
        return platformType == PlatformType.DOCKER;
    }

    public boolean isK8sService() {
        return platformType == PlatformType.K8S;
    }

}