package com.zhoubyte.scorpio_cloud_native.domain.network.repository;

import com.zhoubyte.scorpio_cloud_native.domain.network.entity.Network;

import java.util.List;
import java.util.Optional;

/**
 * 网络仓储接口
 */
public interface NetworkRepository {

    List<Network> findAll();

}