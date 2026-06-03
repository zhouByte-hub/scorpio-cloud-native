package com.zhoubyte.scorpio_cloud_native.infra.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ListImagesCmd;
import com.github.dockerjava.api.model.Image;
import com.zhoubyte.scorpio_cloud_native.domain.image.entity.ContainerImage;
import com.zhoubyte.scorpio_cloud_native.domain.image.repository.ContainerImageRepository;
import com.zhoubyte.scorpio_cloud_native.domain.image.valobj.ImageReference;
import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;

@Component("dockerImageRepository")
public class DockerContainerImageRepository implements ContainerImageRepository {

    private final DockerClient dockerClient;
    private static final String DEFAULT_IMAGE_NAME = "<none>:<none>";

    public DockerContainerImageRepository(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
    }

    @Override
    public List<ContainerImage> queryImageByCondition(String imageName, String imageId, Map<String, String> labels) {
        ListImagesCmd listImagesCmd = dockerClient.listImagesCmd();
        if(labels != null && !labels.isEmpty()) {
            listImagesCmd.withLabelFilter(labels);
        }
        return listImagesCmd.exec().stream()
                .map(item -> {
                    String name = Arrays.stream(item.getRepoTags()).findFirst().orElse(DEFAULT_IMAGE_NAME);
                    ContainerImage.ContainerImageBuilder builder = ContainerImage.builder()
                            .id(item.getId())
                            .imageName(name)
                            .sizeBytes(item.getSize())
                            .labels(item.getLabels())
                            .createdAt(Instant.ofEpochSecond(item.getCreated()));
                    ImageReference imageReference = ImageReference.of(item.getRepoTags(), item.getRepoDigests());
                    builder.reference(imageReference);
                    return builder.build();
                })
                .filter(item -> StringUtils.isEmpty(imageName) || item.getImageName().contains(imageName))
                .filter(item -> StringUtils.isEmpty(imageId) || item.getId().equals(imageId))
//                .filter(item -> {
//                    if (labels == null || labels.isEmpty()) {
//                        return true;
//                    }
//                    Map<String, String> imageLabels = item.getLabels();
//                    if (imageLabels == null || imageLabels.isEmpty()) {
//                        return false;
//                    }
//                    return labels.entrySet().stream().allMatch(entry -> {
//                        String key = entry.getKey();
//                        Object expectedValue = entry.getValue();
//                        String actualValue = imageLabels.get(key);
//
//                        // 如果Label参数只传了 key，没有传入 value，则看镜像的 Label中是否有对应的 key
//                        if (expectedValue == null) {
//                            return imageLabels.containsKey(key);
//                        }
//                        // 匹配两个value是否一致
//                        return Objects.equals(String.valueOf(expectedValue), actualValue);
//                    });
//                })
                .toList();
    }
}
