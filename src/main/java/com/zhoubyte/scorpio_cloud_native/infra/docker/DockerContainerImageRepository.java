package com.zhoubyte.scorpio_cloud_native.infra.docker;

import com.github.dockerjava.api.DockerClient;
import com.zhoubyte.scorpio_cloud_native.domain.image.entity.ContainerImage;
import com.zhoubyte.scorpio_cloud_native.domain.image.repository.ContainerImageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DockerContainerImageRepository implements ContainerImageRepository {

    private DockerClient dockerClient;

    public DockerContainerImageRepository(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
    }

    @Override
    public List<ContainerImage> findAll() {
        return List.of();
    }
}
