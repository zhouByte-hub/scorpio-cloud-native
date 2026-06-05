package com.zhoubyte.scorpio_cloud_native.domain.storage.entity;

import com.zhoubyte.scorpio_cloud_native.domain.storage.valobj.VolumeSpec;
import com.zhoubyte.scorpio_cloud_native.domain.storage.valobj.VolumeType;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.Instant;
import java.util.Map;

/**
 * 存储卷聚合根，统一承载 Docker Volume 和 K8s PV/PVC
 */

@Data
@Builder
public class Volume {

    /** 存储卷唯一标识，Docker 为 Volume Name，K8s 为 PV/PVC UID */
    private String id;

    /** 存储卷名称 */
    private String name;

    /** K8s 命名空间，Docker 场景下可为空 */
    private String namespace;

    /** 存储类型：LOCAL/HOST_PATH/NFS/BLOCK/CLOUD */
    private VolumeType volumeType;

    /** 存储规格配置 */
    private VolumeSpec spec;

    /** 存储状态：Available/Bound/Released/Failed */
    private String status;

    /** 标签，用于资源分类 */
    private Map<String, String> labels;

    /** 创建时间 */
    private Instant createdAt;

}