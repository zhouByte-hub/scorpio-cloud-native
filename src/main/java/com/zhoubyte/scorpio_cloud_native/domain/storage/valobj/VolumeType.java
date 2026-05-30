package com.zhoubyte.scorpio_cloud_native.domain.storage.valobj;

import lombok.Getter;

/**
 * 存储类型枚举，统一 Docker Volume Driver 和 K8s PV Type
 */
@Getter
public enum VolumeType {

    /** 本地存储，Docker 默认存储类型 */
    LOCAL("local"),

    /** 宿主机路径存储 */
    HOST_PATH("hostPath"),

    /** NFS 网络存储 */
    NFS("nfs"),

    /** 块存储 */
    BLOCK("block"),

    /** 云厂商存储，如 AWS EBS、GCE PD */
    CLOUD("cloud");

    private final String value;

    VolumeType(String value) {
        this.value = value;
    }

}