package com.zhoubyte.scorpio_cloud_native.domain.platform.valobj;

import lombok.Getter;

/**
 * 平台类型枚举
 */
@Getter
public enum PlatformType {

    DOCKER("docker"),
    K8S("k8s");

    private final String value;

    PlatformType(String value) {
        this.value = value;
    }

    public static PlatformType fromValue(String value) {
        for (PlatformType type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown platform type: " + value);
    }

}