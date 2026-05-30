package com.zhoubyte.scorpio_cloud_native.domain.workload.valobj;

import lombok.Getter;

/**
 * 工作负载类型枚举
 */
@Getter
public enum WorkloadType {

    /** Docker Container */
    CONTAINER("container"),

    /** K8s Pod */
    POD("pod"),

    /** K8s Deployment */
    DEPLOYMENT("deployment"),

    /** K8s StatefulSet */
    STATEFUL_SET("statefulSet"),

    /** K8s DaemonSet */
    DAEMON_SET("daemonSet"),

    /** K8s Job */
    JOB("job"),

    /** K8s CronJob */
    CRON_JOB("cronJob");

    private final String value;

    WorkloadType(String value) {
        this.value = value;
    }

}