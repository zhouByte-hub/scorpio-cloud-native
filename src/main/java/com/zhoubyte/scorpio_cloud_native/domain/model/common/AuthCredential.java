package com.zhoubyte.scorpio_cloud_native.domain.model.common;

import lombok.Value;

/**
 * 认证凭据值对象，统一承载 Docker Registry 认证和 K8s 集群认证
 */
@Value
public class AuthCredential {

    String username;
    String password;
    String token;
    String email;
    String serverUrl;

    public static AuthCredential ofUserPassword(String username, String password, String serverUrl) {
        return new AuthCredential(username, password, null, null, serverUrl);
    }

    public static AuthCredential ofToken(String token, String serverUrl) {
        return new AuthCredential(null, null, token, null, serverUrl);
    }

    public static AuthCredential ofDockerRegistry(String username, String password, String email, String serverUrl) {
        return new AuthCredential(username, password, null, email, serverUrl);
    }

    public boolean isTokenBased() {
        return token != null && !token.isEmpty();
    }

}
