package com.zhoubyte.scorpio_cloud_native.application.service;

import com.zhoubyte.scorpio_cloud_native.application.exception.ApplicationException;
import com.zhoubyte.scorpio_cloud_native.application.support.PlatformEnum;
import com.zhoubyte.scorpio_cloud_native.domain.node.entity.ComputeNode;
import com.zhoubyte.scorpio_cloud_native.domain.node.repository.ComputeNodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ComputerNodeService {

    private final ComputeNodeRepository dockerComputerNodeRepository;
    private final ComputeNodeRepository k8sComputerNodeRepository;

    public List<ComputeNode> queryComputerNode(String platform) {
        try{
            PlatformEnum platformEnum = PlatformEnum.valueOf(platform);
            if(platformEnum == PlatformEnum.K8S){
                return k8sComputerNodeRepository.queryComputeNode();
            }
            return dockerComputerNodeRepository.queryComputeNode();
        }catch (Exception e){
            throw new ApplicationException(e.getMessage());
        }
    }

}
