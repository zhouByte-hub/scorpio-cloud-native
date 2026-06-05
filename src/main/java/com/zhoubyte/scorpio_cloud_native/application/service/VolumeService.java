package com.zhoubyte.scorpio_cloud_native.application.service;

import com.zhoubyte.scorpio_cloud_native.application.exception.ApplicationException;
import com.zhoubyte.scorpio_cloud_native.application.support.PlatformEnum;
import com.zhoubyte.scorpio_cloud_native.domain.storage.entity.Volume;
import com.zhoubyte.scorpio_cloud_native.domain.storage.repository.VolumeRepository;
import com.zhoubyte.scorpio_cloud_native.facade.request.VolumeRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VolumeService {

    private final VolumeRepository dockerVolumeRepository;
    private final VolumeRepository k8sVolumeRepository;

    public VolumeService(VolumeRepository dockerVolumeRepository, VolumeRepository k8sVolumeRepository) {
        this.dockerVolumeRepository = dockerVolumeRepository;
        this.k8sVolumeRepository = k8sVolumeRepository;
    }

    public List<Volume> listVolumes(VolumeRequest request, String platform) {
        try{
            PlatformEnum platformEnum = PlatformEnum.valueOf(platform);
            if(platformEnum == PlatformEnum.K8S){
                return k8sVolumeRepository.queryStorage(request.getName(), request.getDriver());
            }
            return dockerVolumeRepository.queryStorage(request.getName(), request.getDriver());
        }catch(Exception e){
            throw new ApplicationException(e.getMessage());
        }
    }

}
