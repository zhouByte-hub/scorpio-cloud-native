package com.zhoubyte.scorpio_cloud_native.domain.platform.entity;

import com.zhoubyte.scorpio_cloud_native.domain.platform.valobj.AuthCredential;
import com.zhoubyte.scorpio_cloud_native.domain.platform.valobj.ConnectionEndpoint;
import com.zhoubyte.scorpio_cloud_native.domain.platform.valobj.PlatformType;
import lombok.Builder;
import lombok.Getter;

import java.time.Duration;
import java.util.Map;

/**
 * 平台聚合根，统一承载 Docker 和 K8s 的连接配置信息
 */
@Getter
@Builder
public class Platform {

    /** 平台唯一标识 */
    String id;

    /** 平台名称 */
    String name;

    /** 平台类型：DOCKER/K8S */
    PlatformType platformType;

    /** 连接端点信息 */
    ConnectionEndpoint endpoint;

    /** 认证凭据 */
    AuthCredential credential;

    /** 连接超时时间 */
    Duration connectionTimeout;

    /** 请求超时时间 */
    Duration requestTimeout;

    /** 重试次数上限 */
    long retryLimit;

    /** 重试间隔 */
    Duration retryInterval;

    /** 额外配置，如 K8s 的 kube-config 路径 */
    Map<String, String> extraConfig;

    public boolean isDocker() {
        return platformType == PlatformType.DOCKER;
    }

    public boolean isK8s() {
        return platformType == PlatformType.K8S;
    }

}