package com.zhoubyte.scorpio_cloud_native.domain.workload.entity;

import com.zhoubyte.scorpio_cloud_native.domain.platform.valobj.PlatformType;
import com.zhoubyte.scorpio_cloud_native.domain.workload.valobj.ContainerSpec;
import com.zhoubyte.scorpio_cloud_native.domain.workload.valobj.WorkloadStatus;
import com.zhoubyte.scorpio_cloud_native.domain.workload.valobj.WorkloadType;
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

    /** 工作负载唯一标识，Docker 为 Container ID，K8s 为 Pod UID */
    String id;

    /** 工作负载名称 */
    String name;

    /** K8s 命名空间，Docker 场景下可为空 */
    String namespace;

    /** 平台类型，标识工作负载来源（DOCKER/K8S） */
    PlatformType platformType;

    /** 工作负载类型：CONTAINER/POD/DEPLOYMENT/STATEFUL_SET/DAEMON_SET/JOB/CRON_JOB */
    WorkloadType workloadType;

    /** 容器规格列表，K8s Pod 可包含多个容器，Docker Container 只有一个 */
    List<ContainerSpec> containers;

    /** 工作负载运行状态 */
    WorkloadStatus status;

    /** 标签，用于资源分类和选择器匹配 */
    Map<String, String> labels;

    /** 注解，用于存储非标识性元数据 */
    Map<String, String> annotations;

    /** 创建时间 */
    Instant createdAt;

    /** 更新时间 */
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