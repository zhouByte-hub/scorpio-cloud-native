package com.zhoubyte.scorpio_cloud_native.domain.image.repository;

import com.zhoubyte.scorpio_cloud_native.domain.image.entity.ContainerImage;

import java.util.List;
import java.util.Map;

/**
 * 容器镜像仓储接口
 */
public interface ContainerImageRepository {

    List<ContainerImage> queryImageByCondition(String imageName, String imageId, Map<String, Object> labels);

}
