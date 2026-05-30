package com.zhoubyte.scorpio_cloud_native.domain.storage.valobj;

import lombok.Getter;

/**
 * 存储类型枚举，统一 Docker Volume Driver 和 K8s PV Type
 */
@Getter
public enum VolumeType {

    LOCAL("local"),
    HOST_PATH("hostPath"),
    NFS("nfs"),
    BLOCK("block"),
    CLOUD("cloud");

    private final String value;

    VolumeType(String value) {
        this.value = value;
    }

}