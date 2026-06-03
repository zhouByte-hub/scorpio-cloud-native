package com.zhoubyte.scorpio_cloud_native.application.service;

import com.google.common.collect.Maps;
import com.zhoubyte.scorpio_cloud_native.application.exception.ApplicationException;
import com.zhoubyte.scorpio_cloud_native.application.support.PlatformEnum;
import com.zhoubyte.scorpio_cloud_native.domain.network.entity.CloudNativeNetwork;
import com.zhoubyte.scorpio_cloud_native.domain.network.repository.NetworkRepository;
import com.zhoubyte.scorpio_cloud_native.domain.network.valobj.NetworkType;
import com.zhoubyte.scorpio_cloud_native.facade.request.NetworkRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class NetworkService {

    private final Map<String, NetworkRepository> networkRepositoryMap;

    public NetworkService(NetworkRepository dockerNetworkRepository, NetworkRepository k8sNetworkRepository) {
        this.networkRepositoryMap = Maps.newHashMap();
        networkRepositoryMap.put(PlatformEnum.DOCKER.name(), dockerNetworkRepository);
        networkRepositoryMap.put(PlatformEnum.K8S.name(), k8sNetworkRepository);
    }

    public List<CloudNativeNetwork> queryNetwork(NetworkRequest request, String platform) {
        PlatformEnum platformEnum;
        try {
            platformEnum = PlatformEnum.valueOf(platform);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("不支持的平台类型: " + platform, e);
        }
        NetworkRepository networkRepository = networkRepositoryMap.get(platformEnum.name());
        if (networkRepository == null) {
            throw new ApplicationException(platform + "配置信息未配置");
        }
        NetworkType networkType = NetworkType.fromValue(request.getNetworkType());
        return networkRepository.queryNetworkByCondition(request.getName(), request.getSubnet(), request.getGateway(), networkType);
    }

}
