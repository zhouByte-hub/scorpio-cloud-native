package com.zhoubyte.scorpio_cloud_native.domain.network.repository;

import com.zhoubyte.scorpio_cloud_native.domain.network.entity.Network;

import java.util.List;
import java.util.Optional;

/**
 * 网络仓储接口
 */
public interface NetworkRepository {

    Network save(Network network);

    Optional<Network> findById(String id);

    List<Network> findAll();

    void deleteById(String id);

}