package com.zhoubyte.scorpio_cloud_native.domain.model.image;

import com.zhoubyte.scorpio_cloud_native.domain.model.common.ImageReference;
import com.zhoubyte.scorpio_cloud_native.domain.model.common.PlatformType;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Map;

/**
 * 容器镜像聚合根，统一承载 Docker Image 和 K8s 引用的镜像信息
 */
@Getter
@Builder
public class ContainerImage {

    String id;
    ImageReference reference;
    PlatformType platformType;
    long sizeBytes;
    Instant createdAt;
    Map<String, String> labels;

    public String getFullName() {
        return reference.getFullName();
    }

}
