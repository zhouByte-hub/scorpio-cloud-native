package com.zhoubyte.scorpio_cloud_native.application.service;

import com.zhoubyte.scorpio_cloud_native.application.support.PlatformEnum;
import com.zhoubyte.scorpio_cloud_native.domain.image.entity.ContainerImage;
import com.zhoubyte.scorpio_cloud_native.domain.image.repository.ContainerImageRepository;
import com.zhoubyte.scorpio_cloud_native.facade.request.ImageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ContainerImageRepository dockerImageRepository;
    private final ContainerImageRepository k8sContainerImageRepository;


    public List<ContainerImage> queryContainerImage(ImageRequest request, String platform) {
        try{
            PlatformEnum platformEnum = PlatformEnum.valueOf(platform);
            if(platformEnum.equals(PlatformEnum.K8S)) {
                return k8sContainerImageRepository.findAll(request.getImageName(), request.getImageId(), request.getLabels());
            }
            return dockerImageRepository.findAll(request.getImageName(), request.getImageId(), request.getLabels());
        }catch(Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }



}
