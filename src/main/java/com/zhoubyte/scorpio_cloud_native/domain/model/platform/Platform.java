package com.zhoubyte.scorpio_cloud_native.domain.model.platform;

import com.zhoubyte.scorpio_cloud_native.domain.model.common.AuthCredential;
import com.zhoubyte.scorpio_cloud_native.domain.model.common.ConnectionEndpoint;
import com.zhoubyte.scorpio_cloud_native.domain.model.common.PlatformType;
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

    String id;
    String name;
    PlatformType platformType;
    ConnectionEndpoint endpoint;
    AuthCredential credential;
    Duration connectionTimeout;
    Duration requestTimeout;
    long retryLimit;
    Duration retryInterval;
    Map<String, String> extraConfig;

    public boolean isDocker() {
        return platformType == PlatformType.DOCKER;
    }

    public boolean isK8s() {
        return platformType == PlatformType.K8S;
    }

}
