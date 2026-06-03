package com.zhoubyte.scorpio_cloud_native.facade.endpoint;

import com.zhoubyte.scorpio_cloud_native.application.service.NetworkService;
import com.zhoubyte.scorpio_cloud_native.domain.network.entity.CloudNativeNetwork;
import com.zhoubyte.scorpio_cloud_native.facade.request.NetworkRequest;
import com.zhoubyte.scorpio_cloud_native.facade.response.ListResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/network")
public class CloudNativeNetworkController {

    private final NetworkService networkService;

    public CloudNativeNetworkController(NetworkService networkService) {
        this.networkService = networkService;
    }

    @PostMapping(value = "/list/{platform}:query")
    public ListResponse<CloudNativeNetwork> queryNetwork(@RequestBody NetworkRequest networkRequest, @PathVariable("platform") String platform) {
        List<CloudNativeNetwork> networks = networkService.queryNetwork(networkRequest, platform);
        return ListResponse.of(networks, platform);
    }
}
