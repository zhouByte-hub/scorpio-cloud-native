package com.zhoubyte.scorpio_cloud_native.domain.network.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 网络规格值对象，统一承载 Docker Network 配置和 K8s Service Spec
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NetworkSpec {

    /** 子网列表 */
    private List<SubnetSpec> subnets;

    /** 网络驱动，Docker 为 bridge/overlay/macvlan，K8s 无此概念 */
    private String driver;

    /** 是否为内部网络，不允许外部访问 */
    private boolean internal;

    /** 是否启用 IPv6 */
    private boolean enableIPv6;


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SubnetSpec {
        private String subnet;
        private String gateway;
        private String ipRange;
    }

}