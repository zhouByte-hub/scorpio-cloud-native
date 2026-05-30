package com.zhoubyte.scorpio_cloud_native.domain.workload.valobj;

import lombok.Value;

/**
 * 资源配额值对象，统一承载 Docker 和 K8s 的资源限制
 */
@Value
public class ResourceQuota {

    /** CPU 核数，如 0.5 表示 500m（0.5 核） */
    Double cpuCores;

    /** 内存大小（MB） */
    Long memoryMB;

    public static ResourceQuota of(Double cpuCores, Long memoryMB) {
        return new ResourceQuota(cpuCores, memoryMB);
    }

    public static ResourceQuota unlimited() {
        return new ResourceQuota(null, null);
    }

    public boolean isUnlimited() {
        return cpuCores == null && memoryMB == null;
    }

}