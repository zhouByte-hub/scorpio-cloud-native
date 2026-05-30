package com.zhoubyte.scorpio_cloud_native.domain.storage.valobj;

import lombok.Builder;
import lombok.Value;

import java.util.List;

/**
 * 存储规格值对象，统一承载 Docker Volume 配置和 K8s PV/PVC Spec
 */
@Value
@Builder
public class VolumeSpec {

    String sourcePath;
    String mountPath;
    VolumeType volumeType;
    List<VolumeAccessMode> accessModes;
    Long capacityGB;
    String storageClassName;
    boolean readOnly;

}