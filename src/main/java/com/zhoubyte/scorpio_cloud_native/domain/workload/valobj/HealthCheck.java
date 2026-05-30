package com.zhoubyte.scorpio_cloud_native.domain.workload.valobj;

import lombok.Value;

/**
 * 健康检查值对象，统一承载 Docker HealthCheck 和 K8s Probe
 */
@Value
public class HealthCheck {

    /** 检查类型：HTTP/TCP/COMMAND */
    HealthCheckType type;

    /** 命令检查的命令，如 curl -f http://localhost/ */
    String command;

    /** HTTP 检查的路径，如 /health */
    String httpPath;

    /** HTTP/TCP 检查的端口 */
    int httpPort;

    /** 检查间隔（秒） */
    long intervalSeconds;

    /** 单次检查超时时间（秒） */
    long timeoutSeconds;

    /** 初始延迟时间（秒），容器启动后等待多久开始检查 */
    long initialDelaySeconds;

    /** 连续失败多少次后判定为不健康 */
    int failureThreshold;

    public enum HealthCheckType {
        /** HTTP 请求检查 */
        HTTP,
        /** TCP 端口检查 */
        TCP,
        /** 命令执行检查 */
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