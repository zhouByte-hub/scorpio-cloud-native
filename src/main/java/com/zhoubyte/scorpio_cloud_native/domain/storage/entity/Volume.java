package com.zhoubyte.scorpio_cloud_native.domain.storage.entity;

import com.zhoubyte.scorpio_cloud_native.domain.platform.valobj.PlatformType;
import com.zhoubyte.scorpio_cloud_native.domain.storage.valobj.VolumeSpec;
import com.zhoubyte.scorpio_cloud_native.domain.storage.valobj.VolumeType;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Map;

/**
 * 存储卷聚合根，统一承载 Docker Volume 和 K8s PV/PVC
 */
@Getter
@Builder
public class Volume {

    String id;
    String name;
    String namespace;
    PlatformType platformType;
    VolumeType volumeType;
    VolumeSpec spec;
    String status;
    Map<String, String> labels;
    Instant createdAt;

    public boolean isDockerVolume() {
        return platformType == PlatformType.DOCKER;
    }

    public boolean isK8sVolume() {
        return platformType == PlatformType.K8S;
    }

}