package com.zhoubyte.scorpio_cloud_native.infra.k8s;

import com.zhoubyte.scorpio_cloud_native.domain.network.entity.CloudNativeNetwork;
import com.zhoubyte.scorpio_cloud_native.domain.network.repository.NetworkRepository;
import com.zhoubyte.scorpio_cloud_native.domain.network.valobj.NetworkType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("k8sNetworkRepository")
public class K8sNetworkRepository implements NetworkRepository {
    @Override
    public List<CloudNativeNetwork> queryNetworkByCondition(String name, String subnet, String gateway, NetworkType networkType) {
        return List.of();
    }
}
