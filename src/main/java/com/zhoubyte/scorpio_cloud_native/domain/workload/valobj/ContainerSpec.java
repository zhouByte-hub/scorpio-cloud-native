package com.zhoubyte.scorpio_cloud_native.domain.workload.valobj;

import com.zhoubyte.scorpio_cloud_native.domain.image.valobj.ImageReference;
import com.zhoubyte.scorpio_cloud_native.domain.workload.valobj.ResourceQuota;
import lombok.Builder;
import lombok.Value;

import java.util.List;

/**
 * 容器规格值对象，统一承载 Docker Container 配置和 K8s Container Spec
 */
@Value
@Builder
public class ContainerSpec {

    /** 容器名称 */
    String name;

    /** 镜像引用信息 */
    ImageReference image;

    /** 入口命令，覆盖镜像默认 ENTRYPOINT */
    String command;

    /** 命令参数，覆盖镜像默认 CMD */
    List<String> args;

    /** 环境变量列表 */
    List<EnvVar> envVars;

    /** 端口规格列表 */
    List<PortSpec> ports;

    /** 存储卷挂载列表 */
    List<VolumeSpec> volumes;

    /** 资源限制（上限），对应 Docker --cpus/--memory 或 K8s resources.limits */
    ResourceQuota resourceLimits;

    /** 资源请求（下限），仅 K8s 有效，对应 resources.requests */
    ResourceQuota resourceRequests;

    /** 存活探针，检测容器是否存活，失败则重启 */
    HealthCheck livenessProbe;

    /** 就绪探针，检测容器是否就绪，失败则从 Service endpoints 移除 */
    HealthCheck readinessProbe;

    /** 是否以特权模式运行 */
    boolean privileged;

    /** K8s ServiceAccount 名称，用于 RBAC 权限控制 */
    String serviceAccountName;

}