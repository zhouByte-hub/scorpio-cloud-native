package com.zhoubyte.scorpio_cloud_native.facade.request;

import lombok.Data;

import java.util.Map;

@Data
public class NetworkRequest {

    private String name;
    private String subnet;
    private String gateway;
    private String networkType;

}
