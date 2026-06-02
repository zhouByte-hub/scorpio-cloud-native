package com.zhoubyte.scorpio_cloud_native.domain.image.entity;

import com.zhoubyte.scorpio_cloud_native.domain.image.valobj.ImageReference;
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

    /** 镜像唯一标识，Docker 为 Image ID，K8s 为镜像 Digest */
    String id;

    /** 镜像名称 */
    String imageName;

    /** 镜像引用信息，包含 registry、repository、tag、digest */
    ImageReference reference;

    /** 镜像大小（字节） */
    long sizeBytes;

    /** 镜像创建时间 */
    Instant createdAt;

    /** 镜像标签，键值对形式的元数据 */
    Map<String, String> labels;


}