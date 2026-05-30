package com.zhoubyte.scorpio_cloud_native.domain.storage.valobj;

import lombok.Getter;

/**
 * 存储访问模式枚举，对应 K8s PV Access Modes
 */
@Getter
public enum VolumeAccessMode {

    /** 单节点读写，同一时间只能被一个节点挂载为读写模式 */
    READ_WRITE_ONCE("ReadWriteOnce"),

    /** 多节点读写，可同时被多个节点挂载为读写模式 */
    READ_WRITE_MANY("ReadWriteMany"),

    /** 多节点只读，可同时被多个节点挂载为只读模式 */
    READ_ONLY_MANY("ReadOnlyMany");

    private final String value;

    VolumeAccessMode(String value) {
        this.value = value;
    }

}