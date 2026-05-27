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

    ContainerState state;
    String message;
    String reason;
    int restartCount;
    Instant startedAt;
    Instant finishedAt;
    int readyContainers;
    int totalContainers;
    int availableReplicas;
    int desiredReplicas;

    public boolean isRunning() {
        return state == ContainerState.RUNNING;
    }

    public boolean isHealthy() {
        return state == ContainerState.RUNNING && readyContainers == totalContainers;
    }

}
