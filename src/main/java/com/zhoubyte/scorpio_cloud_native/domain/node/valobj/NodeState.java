package com.zhoubyte.scorpio_cloud_native.domain.node.valobj;

import lombok.Getter;

/**
 * 节点状态枚举，统一 Docker Host 状态和 K8s Node 状态
 */
@Getter
public enum NodeState {

    /** 节点就绪，可接收新 Pod 调度 */
    READY("ready"),

    /** 节点未就绪，不可接收新 Pod */
    NOT_READY("notReady"),

    /** 状态未知 */
    UNKNOWN("unknown");

    private final String value;

    NodeState(String value) {
        this.value = value;
    }

}