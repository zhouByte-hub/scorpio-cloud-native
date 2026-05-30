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

    /** 源路径，HostPath 为宿主机路径，NFS 为远程路径 */
    String sourcePath;

    /** 挂载路径 */
    String mountPath;

    /** 存储类型 */
    VolumeType volumeType;

    /** 访问模式列表 */
    List<VolumeAccessMode> accessModes;

    /** 存储容量（GB） */
    Long capacityGB;

    /** K8s StorageClass 名称 */
    String storageClassName;

    /** 是否只读 */
    boolean readOnly;

}