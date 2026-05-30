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

    NodeState state;
    String message;
    ResourceQuota allocatable;
    ResourceQuota capacity;
    Map<String, String> conditions;

    public boolean isReady() {
        return state == NodeState.READY;
    }

}
