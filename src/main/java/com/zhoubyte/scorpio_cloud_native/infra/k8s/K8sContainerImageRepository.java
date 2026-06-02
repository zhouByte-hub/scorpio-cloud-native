package com.zhoubyte.scorpio_cloud_native.infra.k8s;

import com.zhoubyte.scorpio_cloud_native.domain.image.entity.ContainerImage;
import com.zhoubyte.scorpio_cloud_native.domain.image.repository.ContainerImageRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component("k8sContainerImageRepository")
public class K8sContainerImageRepository implements ContainerImageRepository {

    @Override
    public List<ContainerImage> findAll(String imageName, String imageId, Map<String, Object> labels) {
        return List.of();
    }
}
