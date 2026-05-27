package com.zhoubyte.scorpio_cloud_native.domain.model.common;

import lombok.Value;

/**
 * 环境变量值对象
 */
@Value
public class EnvVar {

    String key;
    String value;
    String valueFrom;

    public static EnvVar of(String key, String value) {
        return new EnvVar(key, value, null);
    }

    public static EnvVar fromRef(String key, String valueFrom) {
        return new EnvVar(key, null, valueFrom);
    }

    public boolean isReference() {
        return valueFrom != null;
    }

}
