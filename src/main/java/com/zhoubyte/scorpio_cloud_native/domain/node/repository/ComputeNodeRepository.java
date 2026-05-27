package com.zhoubyte.scorpio_cloud_native.domain.node.repository;

import com.zhoubyte.scorpio_cloud_native.domain.node.entity.ComputeNode;

import java.util.List;
import java.util.Optional;

/**
 * 计算节点仓储接口
 */
public interface ComputeNodeRepository {

    ComputeNode save(ComputeNode node);

    Optional<ComputeNode> findById(String id);

    List<ComputeNode> findAll();

    void deleteById(String id);

}
