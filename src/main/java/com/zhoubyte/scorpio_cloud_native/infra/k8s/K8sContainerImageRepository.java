package com.zhoubyte.scorpio_cloud_native.infra.k8s;

import com.zhoubyte.scorpio_cloud_native.domain.image.entity.ContainerImage;
import com.zhoubyte.scorpio_cloud_native.domain.image.repository.ContainerImageRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository("k8sImageRepository")
public class K8sContainerImageRepository implements ContainerImageRepository {

    @Override
    public List<ContainerImage> queryImageByCondition(String imageName, String imageId, Map<String, String> labels) {
        return List.of();
    }
}
