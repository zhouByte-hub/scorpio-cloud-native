package com.zhoubyte.scorpio_cloud_native.infra.k8s;

import com.zhoubyte.scorpio_cloud_native.domain.storage.entity.Volume;
import com.zhoubyte.scorpio_cloud_native.domain.storage.repository.VolumeRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("k8sVolumeRepository")
public class K8sVolumeRepository implements VolumeRepository {


    @Override
    public List<Volume> queryStorage(String name, String drive) {
        return List.of();
    }
}
