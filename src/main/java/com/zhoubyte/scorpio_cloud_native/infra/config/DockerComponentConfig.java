package com.zhoubyte.scorpio_cloud_native.infra.config;

import lombok.Data;

@Data
public class DockerComponentConfig {

    private String host;
    private Boolean tlsVerify = true;
    private DockerAuthConfig auth;


    @Data
    public static class DockerAuthConfig {
        private String username;
        private String password;
        private String email;
        private String url;
    }

}
