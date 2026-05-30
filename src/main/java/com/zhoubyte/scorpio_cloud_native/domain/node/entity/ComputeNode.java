package com.zhoubyte.scorpio_cloud_native.domain.node.entity;

import com.zhoubyte.scorpio_cloud_native.domain.node.valobj.NodeStatus;
import com.zhoubyte.scorpio_cloud_native.domain.platform.valobj.PlatformType;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Map;

/**
 * 计算节点聚合根，统一承载 Docker Host 和 K8s Node
 */
@Getter
@Builder
public class ComputeNode {

    String id;
    String name;
    PlatformType platformType;
    NodeStatus status;
    Map<String, String> labels;
    Map<String, String> addresses;
    Instant createdAt;

    public boolean isDockerNode() {
        return platformType == PlatformType.DOCKER;
    }

    public boolean isK8sNode() {
        return platformType == PlatformType.K8S;
    }

}
