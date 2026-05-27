package com.zhoubyte.scorpio_cloud_native.domain.workload.repository;

import com.zhoubyte.scorpio_cloud_native.domain.workload.entity.Workload;

import java.util.List;
import java.util.Optional;

/**
 * 工作负载仓储接口
 */
public interface WorkloadRepository {

    Workload save(Workload workload);

    Optional<Workload> findById(String id);

    List<Workload> findAll();

    void deleteById(String id);

}
