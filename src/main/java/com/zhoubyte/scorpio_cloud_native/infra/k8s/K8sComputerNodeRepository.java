package com.zhoubyte.scorpio_cloud_native.infra.k8s;

import com.zhoubyte.scorpio_cloud_native.domain.node.entity.ComputeNode;
import com.zhoubyte.scorpio_cloud_native.domain.node.repository.ComputeNodeRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository("k8sComputerNodeRepository")
public class K8sComputerNodeRepository implements ComputeNodeRepository {
    @Override
    public List<ComputeNode> queryComputeNode() {
        return List.of();
    }
}
