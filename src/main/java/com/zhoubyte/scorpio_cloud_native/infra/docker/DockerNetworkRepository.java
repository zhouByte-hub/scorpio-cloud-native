package com.zhoubyte.scorpio_cloud_native.infra.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ListNetworksCmd;
import com.github.dockerjava.api.model.Network;
import com.zhoubyte.scorpio_cloud_native.domain.network.entity.CloudNativeNetwork;
import com.zhoubyte.scorpio_cloud_native.domain.network.repository.NetworkRepository;
import com.zhoubyte.scorpio_cloud_native.domain.network.valobj.NetworkSpec;
import com.zhoubyte.scorpio_cloud_native.domain.network.valobj.NetworkType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Component("dockerNetworkRepository")
public class DockerNetworkRepository implements NetworkRepository {

    private final DockerClient dockerClient;

    public DockerNetworkRepository(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
    }

    @Override
    public List<CloudNativeNetwork> queryNetworkByCondition(String name, String subnet, String gateway, NetworkType networkType) {
        ListNetworksCmd listNetworksCmd = dockerClient.listNetworksCmd();
        if (StringUtils.isNotBlank(name)) {
            listNetworksCmd.withNameFilter(name);
        }
        return listNetworksCmd.exec().stream().map(item -> {
                    CloudNativeNetwork.CloudNativeNetworkBuilder labels = CloudNativeNetwork.builder()
                            .id(item.getId())
                            .name(item.getName())
                            .labels(item.getLabels())
                            .createdAt(Instant.ofEpochMilli(item.getCreated().getTime()));

                    // 设置网络规格
                    Network.Ipam ipam = item.getIpam();
                    List<NetworkSpec.SubnetSpec> subnetSpecs;
                    if (ipam != null && ipam.getConfig() != null) {
                        List<Network.Ipam.Config> config = ipam.getConfig();
                        subnetSpecs = config.stream()
                                .map(sub -> new NetworkSpec.SubnetSpec(sub.getSubnet(), sub.getGateway(), sub.getIpRange())).toList();
                    } else {
                        subnetSpecs = Collections.emptyList();
                    }
                    NetworkSpec networkSpec = new NetworkSpec(subnetSpecs, item.getDriver(), item.getInternal(), item.getEnableIPv6());
                    labels.spec(networkSpec);
                    labels.networkType(NetworkType.fromValue(item.getDriver()));
                    return labels.build();
                })
                .filter(item -> networkType == null || item.getNetworkType() == networkType)
                .filter(item -> {
                    if (StringUtils.isEmpty(subnet) && StringUtils.isEmpty(gateway)) {
                        return true;
                    }
                    NetworkSpec spec = item.getSpec();
                    if (spec == null || spec.getSubnets() == null) {
                        return false;
                    }
                    return spec.getSubnets().stream()
                            .anyMatch(sub -> Objects.equals(sub.getSubnet(), subnet) || Objects.equals(sub.getGateway(), gateway));
                })
                .toList();
    }

}
