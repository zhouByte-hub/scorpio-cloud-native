package com.zhoubyte.scorpio_cloud_native.domain.workload.valobj;

import lombok.Value;

/**
 * 存储卷规格值对象，统一承载 Docker Volume 和 K8s PV/PVC
 */
@Value
public class VolumeSpec {

    /** 卷名称 */
    String name;

    /** 源路径，HostPath 为宿主机路径，PVC 为 claim 名称 */
    String sourcePath;

    /** 容器内挂载路径 */
    String mountPath;

    /** 是否只读挂载 */
    boolean readOnly;

    /** 卷类型 */
    VolumeType type;

    public enum VolumeType {
        /** 宿主机路径挂载 */
        HOST_PATH,
        /** 临时目录，Pod 删除后数据丢失 */
        EMPTY_DIR,
        /** 持久化存储声明 */
        PERSISTENT_VOLUME_CLAIM,
        /** 配置文件挂载 */
        CONFIG_MAP,
        /** 密钥挂载 */
        SECRET,
        /** NFS 网络存储 */
        NFS
    }

    public static VolumeSpec hostPath(String name, String sourcePath, String mountPath) {
        return new VolumeSpec(name, sourcePath, mountPath, false, VolumeType.HOST_PATH);
    }

    public static VolumeSpec hostPathReadOnly(String name, String sourcePath, String mountPath) {
        return new VolumeSpec(name, sourcePath, mountPath, true, VolumeType.HOST_PATH);
    }

    public static VolumeSpec pvc(String name, String claimName, String mountPath) {
        return new VolumeSpec(name, claimName, mountPath, false, VolumeType.PERSISTENT_VOLUME_CLAIM);
    }

}