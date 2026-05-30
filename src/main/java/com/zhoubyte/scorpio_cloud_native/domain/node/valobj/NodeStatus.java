package com.zhoubyte.scorpio_cloud_native.domain.node.valobj;

import com.zhoubyte.scorpio_cloud_native.domain.workload.valobj.ResourceQuota;
import lombok.Builder;
import lombok.Value;

import java.util.Map;

/**
 * 节点状态值对象，统一承载 Docker Host 状态和 K8s Node 状态
 */
@Value
@Builder
public class NodeStatus {

    /** 节点状态：READY/NOT_READY/UNKNOWN */
    NodeState state;

    /** 状态详细信息 */
    String message;

    /** 可分配资源，K8s Node Allocatable */
    ResourceQuota allocatable;

    /** 总容量，K8s Node Capacity */
    ResourceQuota capacity;

    /** 条件映射，如 MemoryPressure/DiskPressure/PIDPressure/Ready */
    Map<String, String> conditions;

    public boolean isReady() {
        return state == NodeState.READY;
    }

}