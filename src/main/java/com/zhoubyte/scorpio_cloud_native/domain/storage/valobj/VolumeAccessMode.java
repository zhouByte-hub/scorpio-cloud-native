package com.zhoubyte.scorpio_cloud_native.domain.storage.valobj;

import lombok.Getter;

/**
 * 存储访问模式枚举，对应 K8s PV Access Modes
 */
@Getter
public enum VolumeAccessMode {

    READ_WRITE_ONCE("ReadWriteOnce"),
    READ_WRITE_MANY("ReadWriteMany"),
    READ_ONLY_MANY("ReadOnlyMany");

    private final String value;

    VolumeAccessMode(String value) {
        this.value = value;
    }

}