package com.zhoubyte.scorpio_cloud_native.domain.image.repository;

import com.zhoubyte.scorpio_cloud_native.domain.image.entity.ContainerImage;

import java.util.List;

/**
 * 容器镜像仓储接口
 */
public interface ContainerImageRepository {

    List<ContainerImage> findAll();

}
