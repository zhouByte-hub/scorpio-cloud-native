package com.zhoubyte.scorpio_cloud_native.domain.platform.repository;

import com.zhoubyte.scorpio_cloud_native.domain.platform.entity.Platform;

import java.util.List;
import java.util.Optional;

/**
 * 平台仓储接口
 */
public interface PlatformRepository {

    Platform save(Platform platform);

    Optional<Platform> findById(String id);

    List<Platform> findAll();

    void deleteById(String id);

}
