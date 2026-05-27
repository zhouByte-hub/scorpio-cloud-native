package com.zhoubyte.scorpio_cloud_native.domain.platform.valobj;

import lombok.Value;

/**
 * 连接端点值对象，统一承载 Docker Host 连接和 K8s Master 连接
 */
@Value
public class ConnectionEndpoint {

    String host;
    int port;
    boolean tlsEnabled;
    String certificatePath;

    public static ConnectionEndpoint of(String host, int port, boolean tlsEnabled) {
        return new ConnectionEndpoint(host, port, tlsEnabled, null);
    }

    public static ConnectionEndpoint of(String host, boolean tlsEnabled) {
        return new ConnectionEndpoint(host, -1, tlsEnabled, null);
    }

    public String getAddress() {
        if (port > 0) {
            return host + ":" + port;
        }
        return host;
    }

}
