package com.zhoubyte.scorpio_cloud_native.domain.workload.valobj;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

/**
 * 工作负载状态值对象，统一承载 Docker 容器状态和 K8s Pod 状态
 */
@Value
@Builder
public class WorkloadStatus {

    /** 容器状态：RUNNING/WAITING/TERMINATED/PAUSED/RESTARTING/DEAD/CREATED/UNKNOWN */
    ContainerState state;

    /** 状态详细信息 */
    String message;

    /** 状态原因，如 CrashLoopBackOff、ImagePullBackOff 等 */
    String reason;

    /** 重启次数 */
    int restartCount;

    /** 启动时间 */
    Instant startedAt;

    /** 结束时间，仅 TERMINATED 状态有效 */
    Instant finishedAt;

    /** 就绪容器数量 */
    int readyContainers;

    /** 总容器数量 */
    int totalContainers;

    /** 可用副本数，仅 Deployment 有效 */
    int availableReplicas;

    /** 期望副本数，仅 Deployment 有效 */
    int desiredReplicas;

    public boolean isRunning() {
        return state == ContainerState.RUNNING;
    }

    public boolean isHealthy() {
        return state == ContainerState.RUNNING && readyContainers == totalContainers;
    }

}