package com.zhoubyte.scorpio_cloud_native.domain.network.repository;

import com.zhoubyte.scorpio_cloud_native.domain.network.entity.CloudNativeNetwork;
import com.zhoubyte.scorpio_cloud_native.domain.network.valobj.NetworkType;

import java.util.List;

/**
 * 网络仓储接口
 */
public interface NetworkRepository {

    List<CloudNativeNetwork> queryNetworkByCondition(String name, String subnet, String gateway, NetworkType networkType);

}