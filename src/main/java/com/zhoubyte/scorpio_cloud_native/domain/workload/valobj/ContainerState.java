package com.zhoubyte.scorpio_cloud_native.domain.workload.valobj;

import lombok.Getter;

/**
 * 容器状态枚举，统一 Docker 和 K8s 的容器状态
 */
@Getter
public enum ContainerState {

    /** 运行中 */
    RUNNING("running"),

    /** 等待中，如等待镜像拉取 */
    WAITING("waiting"),

    /** 已终止 */
    TERMINATED("terminated"),

    /** 已暂停（Docker 特有） */
    PAUSED("paused"),

    /** 重启中（Docker 特有） */
    RESTARTING("restarting"),

    /** 已死亡（Docker 特有） */
    DEAD("dead"),

    /** 已创建但未启动（Docker 特有） */
    CREATED("created"),

    /** 未知状态 */
    UNKNOWN("unknown");

    private final String value;

    ContainerState(String value) {
        this.value = value;
    }

}