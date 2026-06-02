package com.zhoubyte.scorpio_cloud_native.facade.enums;

import lombok.Getter;

@Getter
public enum ErrorCode {

    SUCCESS(0, "Success"),
    
    BAD_REQUEST(40000, "Bad request"),
    VALIDATION_ERROR(40001, "Request validation failed"),
    ILLEGAL_ARGUMENT(40002, "Invalid argument provided"),
    DOMAIN_ERROR(40003, "Domain rule violation"),
    
    NOT_FOUND(40400, "Resource not found"),
    
    INTERNAL_ERROR(50000, "Internal server error"),
    APP_ERROR(50001, "Application operation failed"),
    INFRA_ERROR(50002, "Infrastructure operation failed"),
    
    SERVICE_UNAVAILABLE(50300, "Service unavailable");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

}