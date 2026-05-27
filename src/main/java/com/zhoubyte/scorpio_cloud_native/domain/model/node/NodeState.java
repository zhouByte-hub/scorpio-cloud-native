package com.zhoubyte.scorpio_cloud_native.domain.model.node;

import lombok.Getter;

/**
 * 节点状态枚举，统一 Docker Host 状态和 K8s Node 状态
 */
@Getter
public enum NodeState {

    READY("ready"),
    NOT_READY("notReady"),
    UNKNOWN("unknown");

    private final String value;

    NodeState(String value) {
        this.value = value;
    }

}
