package com.zhoubyte.scorpio_cloud_native.facade.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ListResponse<T> {

    private List<T> items;
    private Integer total;
    private String platform;

    public static <T> ListResponse<T> of(List<T> items, String platform) {
        return ListResponse.<T>builder()
                .items(items)
                .total(items.size())
                .platform(platform)
                .build();
    }

}
