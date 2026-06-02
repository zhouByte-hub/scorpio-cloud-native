package com.zhoubyte.scorpio_cloud_native.infra.config.common;

import lombok.Data;

@Data
public class K8sComponentConfig {

    private String master;
    private Boolean disableAutoConfig = false;
    private String apiVersion = "v1";
    private Boolean trustCertificates = true;
    private String kubeConfig = "";
    private Long connectionTimeout = 10000L;
    private Long requestTimeout = 10000L;
    private Long retryLimit = 10L;
    private Long retryInterval = 100L;
    private K8sAuthConfig auth;
    private K8sWatchConfig watch;


    @Data
    public static class K8sAuthConfig {
        private Boolean tryServiceAccount = true;
        private Boolean tryKubeConfig = true;
    }

    @Data
    public static class K8sWatchConfig {
        private Long reconnectInterval = 1000L;
        private Long reconnectLimit = -1L;
    }

}
