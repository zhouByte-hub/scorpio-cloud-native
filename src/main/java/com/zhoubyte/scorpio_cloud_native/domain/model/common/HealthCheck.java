package com.zhoubyte.scorpio_cloud_native.domain.model.common;

import lombok.Value;

/**
 * 健康检查值对象，统一承载 Docker HealthCheck 和 K8s Probe
 */
@Value
public class HealthCheck {

    HealthCheckType type;
    String command;
    String httpPath;
    int httpPort;
    long intervalSeconds;
    long timeoutSeconds;
    long initialDelaySeconds;
    int failureThreshold;

    public enum HealthCheckType {
        HTTP,
        TCP,
        COMMAND
    }

    public static HealthCheck http(String path, int port, long intervalSeconds, long timeoutSeconds) {
        return new HealthCheck(HealthCheckType.HTTP, null, path, port, intervalSeconds, timeoutSeconds, 0, 3);
    }

    public static HealthCheck tcp(int port, long intervalSeconds, long timeoutSeconds) {
        return new HealthCheck(HealthCheckType.TCP, null, null, port, intervalSeconds, timeoutSeconds, 0, 3);
    }

    public static HealthCheck command(String command, long intervalSeconds, long timeoutSeconds) {
        return new HealthCheck(HealthCheckType.COMMAND, command, null, -1, intervalSeconds, timeoutSeconds, 0, 3);
    }

}
