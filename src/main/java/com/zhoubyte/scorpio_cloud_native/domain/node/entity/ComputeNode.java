package com.zhoubyte.scorpio_cloud_native.domain.node.entity;

import com.zhoubyte.scorpio_cloud_native.domain.node.valobj.NodeStatus;
import lombok.Builder;
import lombok.Data;
import java.util.List;
import java.util.Map;

/**
 * 计算节点聚合根，统一承载 Docker Host 和 K8s Node
 */
@Data
@Builder
public class ComputeNode {

    /** 节点唯一标识，Docker 为 Host ID，K8s 为 Node UID */
    private String id;

    /** 节点名称 */
    private String name;

    /** 镜像数量 */
    private Integer imageCount;

    /** 容器 数量/Pod 数量 */
    private Integer containerCount;

    /** 版本 */
    private String version;

    /** 节点状态 */
    private NodeStatus status;

    /** 标签，用于节点分类和调度选择 */
    private Map<String, String> labels;

    /** 地址映射，如 InternalIP/ExternalIP/Hostname */
    private List<String> addresses;

    /** 插件信息 */
    private Map<String, List<String>> plugins;

}