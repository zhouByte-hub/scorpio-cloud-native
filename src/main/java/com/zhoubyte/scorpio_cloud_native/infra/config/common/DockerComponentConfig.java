package com.zhoubyte.scorpio_cloud_native.infra.config.common;

import lombok.Data;

@Data
public class DockerComponentConfig {

    private String host;
    private Boolean tlsVerify = true;
    private DockerAuthConfig auth;
    private DockerHttpConfig http;
    private DockerSslConfig ssl;
    private String certPath = "/home/user/.docker";


    @Data
    public static class DockerAuthConfig {
        private String username;
        private String password;
        private String email;
        private String url;
    }

    @Data
    public static class DockerHttpConfig {

        private Long connectionTimeout = 10000L;
        private Long responseTimeout = 5000L;
        private int maxConnect = 10;

    }

    @Data
    public static class DockerSslConfig {
        private Boolean enabled = false;
        private String protocol = "TLSv1.2";
        private KeyStoreConfig keyStore;
        private TrustStoreConfig trustStore;
        private Boolean verifyHostname = true;

        @Data
        public static class KeyStoreConfig {
            private String path;
            private String password;
            private String type = "JKS";
        }

        @Data
        public static class TrustStoreConfig {
            private String path;
            private String password;
            private String type = "JKS";
        }
    }

}
