package com.zhoubyte.scorpio_cloud_native.application.service;

import com.zhoubyte.scorpio_cloud_native.application.exception.ApplicationException;
import com.zhoubyte.scorpio_cloud_native.application.support.PlatformEnum;
import com.zhoubyte.scorpio_cloud_native.domain.image.entity.ContainerImage;
import com.zhoubyte.scorpio_cloud_native.domain.image.repository.ContainerImageRepository;
import com.zhoubyte.scorpio_cloud_native.facade.request.ImageRequest;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ImageService {

    private final Map<String, ContainerImageRepository> imageRepositoryMap;

    public ImageService(ContainerImageRepository dockerImageRepository, ContainerImageRepository k8sImageRepository) {
        imageRepositoryMap = new HashMap<>();
        imageRepositoryMap.put(PlatformEnum.DOCKER.name(), dockerImageRepository);
        imageRepositoryMap.put(PlatformEnum.K8S.name(), k8sImageRepository);
    }


    public List<ContainerImage> queryContainerImage(ImageRequest request, String platform) {
        if (request == null) {
            throw new IllegalArgumentException("request不能为空");
        }
        PlatformEnum platformEnum;
        try {
            platformEnum = PlatformEnum.valueOf(platform);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("不支持的平台类型: " + platform, e);
        }
        ContainerImageRepository containerImageRepository = imageRepositoryMap.get(platformEnum.name());
        if (containerImageRepository == null) {
            throw new ApplicationException(platform + "配置信息未配置");
        }
        return containerImageRepository.queryImageByCondition(request.getImageName(), request.getImageId(), request.getLabels());
    }



}
