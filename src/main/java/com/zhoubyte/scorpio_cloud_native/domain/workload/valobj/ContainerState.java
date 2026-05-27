package com.zhoubyte.scorpio_cloud_native.domain.workload.valobj;

import lombok.Getter;

/**
 * 容器状态枚举，统一 Docker 和 K8s 的容器状态
 */
@Getter
public enum ContainerState {

    RUNNING("running"),
    WAITING("waiting"),
    TERMINATED("terminated"),
    PAUSED("paused"),
    RESTARTING("restarting"),
    DEAD("dead"),
    CREATED("created"),
    UNKNOWN("unknown");

    private final String value;

    ContainerState(String value) {
        this.value = value;
    }

}
