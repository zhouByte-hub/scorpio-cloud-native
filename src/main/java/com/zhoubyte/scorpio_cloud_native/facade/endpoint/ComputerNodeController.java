package com.zhoubyte.scorpio_cloud_native.facade.endpoint;

import com.zhoubyte.scorpio_cloud_native.application.service.ComputerNodeService;
import com.zhoubyte.scorpio_cloud_native.domain.node.entity.ComputeNode;
import com.zhoubyte.scorpio_cloud_native.facade.response.ListResponse;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/node")
public class ComputerNodeController {

    @Resource
    private ComputerNodeService computerNodeService;

    @GetMapping(value = "/list/{platform}:query")
    public ListResponse<ComputeNode> queryComputerNode(@PathVariable("platform") String platform) {
        List<ComputeNode> computeNodes = computerNodeService.queryComputerNode(platform);
        return ListResponse.of(computeNodes, platform);
    }

}
