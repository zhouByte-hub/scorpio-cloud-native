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

    String name;
    ImageReference image;
    String command;
    List<String> args;
    List<EnvVar> envVars;
    List<PortSpec> ports;
    List<VolumeSpec> volumes;
    ResourceQuota resourceLimits;
    ResourceQuota resourceRequests;
    HealthCheck livenessProbe;
    HealthCheck readinessProbe;
    boolean privileged;
    String serviceAccountName;

}
