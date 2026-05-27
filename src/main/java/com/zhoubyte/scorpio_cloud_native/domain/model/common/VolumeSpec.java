package com.zhoubyte.scorpio_cloud_native.domain.model.common;

import lombok.Value;

/**
 * 存储卷规格值对象，统一承载 Docker Volume 和 K8s PV/PVC
 */
@Value
public class VolumeSpec {

    String name;
    String sourcePath;
    String mountPath;
    boolean readOnly;
    VolumeType type;

    public enum VolumeType {
        HOST_PATH,
        EMPTY_DIR,
        PERSISTENT_VOLUME_CLAIM,
        CONFIG_MAP,
        SECRET,
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
