package com.zhoubyte.scorpio_cloud_native.infra.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Info;
import com.github.dockerjava.api.model.InfoRegistryConfig;
import com.zhoubyte.scorpio_cloud_native.domain.node.entity.ComputeNode;
import com.zhoubyte.scorpio_cloud_native.domain.node.repository.ComputeNodeRepository;
import com.zhoubyte.scorpio_cloud_native.domain.node.valobj.NodeState;
import com.zhoubyte.scorpio_cloud_native.domain.node.valobj.NodeStatus;
import com.zhoubyte.scorpio_cloud_native.domain.workload.valobj.ResourceQuota;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository("dockerComputerNodeRepository")
public class DockerComputerNodeRepository implements ComputeNodeRepository {

    public final DockerClient dockerClient;

    public DockerComputerNodeRepository(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
    }

    /**
     * 对于非 Swarm 模式的 Docker， 没有"节点"的概念
     * Docker 本身就是单机引擎。这种情况下，应该获取的是 Docker 主机信息 或者 容器信息 。
     */
    @Override
    public List<ComputeNode> queryComputeNode() {
        Info info = dockerClient.infoCmd().exec();
        ComputeNode.ComputeNodeBuilder computeNodeBuilder = ComputeNode.builder()
                .id(info.getId())
                .name(info.getName())
                .plugins(info.getPlugins())
                .imageCount(info.getImages())
                .version(info.getServerVersion())
                .containerCount(info.getContainers());

        // 设置 Labels
        Map<String, String> labels = new HashMap<>();
        if (info.getLabels() != null && info.getLabels().length != 0) {
            for (String label : info.getLabels()) {
                String[] split = label.split("=");
                if(split.length != 2) {
                    continue;
                }
                labels.put(split[0], split[1]);
            }
        }else {
            labels = Collections.emptyMap();
        }
        computeNodeBuilder.labels(labels);

        // 设置 status
        NodeStatus build = NodeStatus.builder()
                .capacity(ResourceQuota.of(info.getNCPU() == null ? 0.0D : info.getNCPU(), info.getMemTotal()))
                .allocatable(ResourceQuota.of(info.getNCPU() == null ? 0.0D : info.getNCPU(), info.getMemTotal()))
                .state(NodeState.UNKNOWN)
                .conditions(generatorCondition(info))
                .message(formatNodeStatusMessage(info)).build();
        computeNodeBuilder.status(build);

        // 设置 Address 信息
        InfoRegistryConfig registryConfig = info.getRegistryConfig();
        if (registryConfig != null) {
            computeNodeBuilder.addresses(registryConfig.getInsecureRegistryCIDRs());
        }
        return List.of(computeNodeBuilder.build());
    }

    private String formatNodeStatusMessage(Info info) {
        return String.format("architecture：%s，dockerRootDir：%s", info.getArchitecture(), info.getDockerRootDir());
    }

    private Map<String, String> generatorCondition(Info info) {
        Map<String, String> conditions = new HashMap<>();
        conditions.put("containersStopped", String.valueOf(info.getContainersStopped()));
        conditions.put("containersRunning", String.valueOf(info.getContainersRunning()));
        conditions.put("cpuCfsPeriod", String.valueOf(info.getCpuCfsPeriod()));
        conditions.put("cpuCfsQuota", String.valueOf(info.getCpuCfsQuota()));
        conditions.put("cpuShares", String.valueOf(info.getCpuShares()));
        conditions.put("cpuSet", String.valueOf(info.getCpuSet()));
        conditions.put("debug", String.valueOf(info.getDebug()));
        conditions.put("httpProxy", String.valueOf(info.getHttpProxy()));
        conditions.put("httpsProxy", String.valueOf(info.getHttpsProxy()));
        conditions.put("osType", String.valueOf(info.getOsType()));
        return conditions;
    }

}
