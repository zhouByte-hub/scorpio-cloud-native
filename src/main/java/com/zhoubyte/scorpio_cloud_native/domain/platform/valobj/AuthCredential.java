package com.zhoubyte.scorpio_cloud_native.domain.platform.valobj;

import lombok.Value;

/**
 * 认证凭据值对象，统一承载 Docker Registry 认证和 K8s 集群认证
 */
@Value
public class AuthCredential {

    /** 用户名 */
    String username;

    /** 密码 */
    String password;

    /** Token，如 K8s ServiceAccount Token */
    String token;

    /** 邮箱，Docker Registry 认证时使用 */
    String email;

    /** 服务器地址，如 Docker Registry URL 或 K8s API Server URL */
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