package com.zhoubyte.scorpio_cloud_native.domain.storage.repository;

import com.zhoubyte.scorpio_cloud_native.domain.storage.entity.Volume;

import java.util.List;
import java.util.Optional;

/**
 * 存储卷仓储接口
 */
public interface VolumeRepository {

    Volume save(Volume volume);

    Optional<Volume> findById(String id);

    List<Volume> findAll();

    void deleteById(String id);

}