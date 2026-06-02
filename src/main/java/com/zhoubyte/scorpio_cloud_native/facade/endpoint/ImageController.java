package com.zhoubyte.scorpio_cloud_native.facade.endpoint;

import com.zhoubyte.scorpio_cloud_native.application.service.ImageService;
import com.zhoubyte.scorpio_cloud_native.domain.image.entity.ContainerImage;
import com.zhoubyte.scorpio_cloud_native.facade.request.ImageRequest;
import com.zhoubyte.scorpio_cloud_native.facade.response.ImageListResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/image")
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping(value = "/list/{platform}:query")
    public ImageListResponse imageList(@RequestBody ImageRequest imageRequest, @PathVariable("platform") String platform) {
        List<ContainerImage> containerImages = imageService.queryContainerImage(imageRequest, platform);
        return ImageListResponse.of(containerImages, platform);
    }


}
