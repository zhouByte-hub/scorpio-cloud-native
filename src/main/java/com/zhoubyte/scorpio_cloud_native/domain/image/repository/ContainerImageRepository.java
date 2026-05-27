package com.zhoubyte.scorpio_cloud_native.domain.image.repository;

import com.zhoubyte.scorpio_cloud_native.domain.image.entity.ContainerImage;

import java.util.List;
import java.util.Optional;

/**
 * 容器镜像仓储接口
 */
public interface ContainerImageRepository {

    ContainerImage save(ContainerImage image);

    Optional<ContainerImage> findById(String id);

    List<ContainerImage> findAll();

    void deleteById(String id);

}
