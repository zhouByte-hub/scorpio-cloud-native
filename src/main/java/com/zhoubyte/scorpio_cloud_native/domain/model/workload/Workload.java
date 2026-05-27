package com.zhoubyte.scorpio_cloud_native.domain.model.workload;

import com.zhoubyte.scorpio_cloud_native.domain.model.common.PlatformType;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * 工作负载聚合根，统一承载 Docker Container 和 K8s Pod/Deployment
 */
@Getter
@Builder
public class Workload {

    String id;
    String name;
    String namespace;
    PlatformType platformType;
    WorkloadType workloadType;
    List<ContainerSpec> containers;
    WorkloadStatus status;
    Map<String, String> labels;
    Map<String, String> annotations;
    Instant createdAt;
    Instant updatedAt;

    public boolean isDockerWorkload() {
        return platformType == PlatformType.DOCKER;
    }

    public boolean isK8sWorkload() {
        return platformType == PlatformType.K8S;
    }

    public ContainerSpec getPrimaryContainer() {
        if (containers == null || containers.isEmpty()) {
            return null;
        }
        return containers.getFirst();
    }

}
