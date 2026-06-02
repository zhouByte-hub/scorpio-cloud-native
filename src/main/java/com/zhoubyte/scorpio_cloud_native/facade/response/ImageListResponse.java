package com.zhoubyte.scorpio_cloud_native.facade.response;

import com.zhoubyte.scorpio_cloud_native.domain.image.entity.ContainerImage;
import lombok.Builder;
import lombok.Data;

import java.awt.*;
import java.util.List;

@Data
@Builder
public class ImageListResponse {

    private List<ContainerImage> images;
    private Integer total;
    private String platform;

    public static ImageListResponse of(List<ContainerImage> images, String platform) {
        return ImageListResponse.builder()
                .images(images)
                .total(images.size())
                .platform(platform)
                .build();
    }


}