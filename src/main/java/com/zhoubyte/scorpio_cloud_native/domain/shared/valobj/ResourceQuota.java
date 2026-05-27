package com.zhoubyte.scorpio_cloud_native.domain.shared.valobj;

import lombok.Value;

/**
 * 资源配额值对象，统一承载 Docker 和 K8s 的资源限制
 */
@Value
public class ResourceQuota {

    Double cpuCores;
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
