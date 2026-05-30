package com.zhoubyte.scorpio_cloud_native.domain.workload.valobj;

import lombok.Value;

/**
 * 环境变量值对象
 */
@Value
public class EnvVar {

    /** 变量名 */
    String key;

    /** 变量值，直接指定 */
    String value;

    /** 值来源，K8s 可引用 ConfigMap/Secret，如 configMapKeyRef.name/key */
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