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

    /** 节点唯一标识，Docker 为 Host ID，K8s 为 Node UID */
    String id;

    /** 节点名称 */
    String name;

    /** 平台类型，标识节点来源（DOCKER/K8S） */
    PlatformType platformType;

    /** 节点状态 */
    NodeStatus status;

    /** 标签，用于节点分类和调度选择 */
    Map<String, String> labels;

    /** 地址映射，如 InternalIP/ExternalIP/Hostname */
    Map<String, String> addresses;

    /** 创建时间 */
    Instant createdAt;

    public boolean isDockerNode() {
        return platformType == PlatformType.DOCKER;
    }

    public boolean isK8sNode() {
        return platformType == PlatformType.K8S;
    }

}