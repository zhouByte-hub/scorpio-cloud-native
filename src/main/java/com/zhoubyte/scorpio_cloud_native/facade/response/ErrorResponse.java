package com.zhoubyte.scorpio_cloud_native.facade.response;

import com.zhoubyte.scorpio_cloud_native.facade.enums.ErrorCode;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Builder
public class ErrorResponse {

    private Integer code;
    private String message;
    private String detail;
    private String timestamp;
    private String path;

    private static final DateTimeFormatter FORMATTER = 
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static ErrorResponse of(ErrorCode errorCode, String detail, String path) {
        return ErrorResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .detail(detail)
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .path(path)
                .build();
    }

}