package com.zhoubyte.scorpio_cloud_native.domain.model.workload;

import lombok.Getter;

/**
 * 工作负载类型枚举
 */
@Getter
public enum WorkloadType {

    CONTAINER("container"),
    POD("pod"),
    DEPLOYMENT("deployment"),
    STATEFUL_SET("statefulSet"),
    DAEMON_SET("daemonSet"),
    JOB("job"),
    CRON_JOB("cronJob");

    private final String value;

    WorkloadType(String value) {
        this.value = value;
    }

}
