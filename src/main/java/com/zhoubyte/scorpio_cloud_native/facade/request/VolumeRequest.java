package com.zhoubyte.scorpio_cloud_native.facade.request;

import lombok.Data;

@Data
public class VolumeRequest {

    private String name;
    private String driver;

}
