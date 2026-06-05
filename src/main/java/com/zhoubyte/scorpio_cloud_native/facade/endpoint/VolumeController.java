package com.zhoubyte.scorpio_cloud_native.facade.endpoint;

import com.zhoubyte.scorpio_cloud_native.application.service.VolumeService;
import com.zhoubyte.scorpio_cloud_native.domain.storage.entity.Volume;
import com.zhoubyte.scorpio_cloud_native.facade.request.VolumeRequest;
import com.zhoubyte.scorpio_cloud_native.facade.response.ListResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/volume")
public class VolumeController {

    private final VolumeService volumeService;

    public VolumeController(VolumeService volumeService) {
        this.volumeService = volumeService;
    }

    @PostMapping(value = "/list/{platform}:query")
    public ListResponse<Volume> queryVolumesList(@RequestBody VolumeRequest request, @PathVariable("platform") String platform) {
        List<Volume> volumes = volumeService.listVolumes(request, platform);
        return ListResponse.of(volumes, platform);
    }
}
