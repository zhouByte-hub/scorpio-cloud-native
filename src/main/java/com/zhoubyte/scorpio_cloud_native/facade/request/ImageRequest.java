package com.zhoubyte.scorpio_cloud_native.facade.request;

import lombok.Data;

import java.util.Map;

@Data
public class ImageRequest {

    private String imageName;
    private String imageId;
    private Map<String, String> labels;
    
}
