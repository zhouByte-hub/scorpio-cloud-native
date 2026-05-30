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

    /** 存储卷唯一标识，Docker 为 Volume Name，K8s 为 PV/PVC UID */
    String id;

    /** 存储卷名称 */
    String name;

    /** K8s 命名空间，Docker 场景下可为空 */
    String namespace;

    /** 平台类型，标识存储卷来源（DOCKER/K8S） */
    PlatformType platformType;

    /** 存储类型：LOCAL/HOST_PATH/NFS/BLOCK/CLOUD */
    VolumeType volumeType;

    /** 存储规格配置 */
    VolumeSpec spec;

    /** 存储状态：Available/Bound/Released/Failed */
    String status;

    /** 标签，用于资源分类 */
    Map<String, String> labels;

    /** 创建时间 */
    Instant createdAt;

    public boolean isDockerVolume() {
        return platformType == PlatformType.DOCKER;
    }

    public boolean isK8sVolume() {
        return platformType == PlatformType.K8S;
    }

}