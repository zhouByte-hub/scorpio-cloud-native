package com.zhoubyte.scorpio_cloud_native.infra.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectVolumeResponse;
import com.github.dockerjava.api.command.ListVolumesResponse;
import com.zhoubyte.scorpio_cloud_native.domain.storage.entity.Volume;
import com.zhoubyte.scorpio_cloud_native.domain.storage.repository.VolumeRepository;
import com.zhoubyte.scorpio_cloud_native.domain.storage.valobj.VolumeSpec;
import com.zhoubyte.scorpio_cloud_native.domain.storage.valobj.VolumeType;
import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Stream;

@Repository("dockerVolumeRepository")
public class DockerVolumeRepository implements VolumeRepository {

    private final DockerClient dockerClient;

    public DockerVolumeRepository(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
    }

    @Override
    public List<Volume> queryStorage(String name, String drive) {
        ListVolumesResponse exec = dockerClient.listVolumesCmd().exec();
        if(exec == null || exec.getVolumes() == null) {
            return List.of();
        }
        Stream<InspectVolumeResponse> stream = exec.getVolumes().stream();
        if(!StringUtils.isEmpty(name)) {
            stream = stream.filter(volume -> volume.getName().equals(name));
        }
        if(!StringUtils.isEmpty(drive)) {
            stream = stream.filter(volume -> volume.getDriver().equals(drive));
        }
        return stream.map(item -> {
            VolumeType volumeType = VolumeType.fromValue(item.getDriver());
            Volume.VolumeBuilder builder = Volume.builder()
                    .id(item.getName())
                    .name(item.getName())
                    .labels(item.getLabels())
                    .volumeType(volumeType);
            VolumeSpec.VolumeSpecBuilder volumeSpecBuilder = VolumeSpec.builder()
                    .sourcePath(item.getMountpoint())
                    .volumeType(volumeType);
            builder.spec(volumeSpecBuilder.build());
            return builder.build();
        }).toList();
    }
}
