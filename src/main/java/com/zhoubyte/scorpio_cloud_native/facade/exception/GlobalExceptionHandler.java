package com.zhoubyte.scorpio_cloud_native.facade.exception;

import com.zhoubyte.scorpio_cloud_native.application.exception.ApplicationException;
import com.zhoubyte.scorpio_cloud_native.domain.exception.DomainException;
import com.zhoubyte.scorpio_cloud_native.facade.enums.ErrorCode;
import com.zhoubyte.scorpio_cloud_native.facade.response.ErrorResponse;
import com.zhoubyte.scorpio_cloud_native.infra.exception.InfrastructureException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(
            DomainException ex, HttpServletRequest request) {
        
        log.error("Domain exception occurred: {}", ex.getMessage(), ex);
        
        ErrorResponse response = ErrorResponse.of(
                ErrorCode.DOMAIN_ERROR, 
                ex.getMessage(), 
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorResponse> handleApplicationException(
            ApplicationException ex, HttpServletRequest request) {
        
        log.error("Application exception occurred: {}", ex.getMessage(), ex);
        
        ErrorResponse response = ErrorResponse.of(
                ErrorCode.APP_ERROR, 
                ex.getMessage(), 
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(InfrastructureException.class)
    public ResponseEntity<ErrorResponse> handleInfrastructureException(
            InfrastructureException ex, HttpServletRequest request) {
        
        log.error("Infrastructure exception occurred: {}", ex.getMessage(), ex);
        
        ErrorResponse response = ErrorResponse.of(
                ErrorCode.INFRA_ERROR, 
                ex.getMessage(), 
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        
        log.warn("Validation failed: {}", ex.getMessage());
        
        String detail = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("Validation failed");
        
        ErrorResponse response = ErrorResponse.of(
                ErrorCode.VALIDATION_ERROR, 
                detail, 
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, HttpServletRequest request) {
        
        log.warn("Illegal argument: {}", ex.getMessage());
        
        ErrorResponse response = ErrorResponse.of(
                ErrorCode.ILLEGAL_ARGUMENT, 
                ex.getMessage(), 
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {
        
        log.error("Unexpected exception occurred: {}", ex.getMessage(), ex);
        
        ErrorResponse response = ErrorResponse.of(
                ErrorCode.INTERNAL_ERROR, 
                ex.getMessage(), 
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

}