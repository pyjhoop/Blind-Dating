package com.blind.dating.exception;

import com.blind.dating.common.Api;
import com.blind.dating.common.code.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@Order(value = 0)
public class ApiExceptionHandler {

    @ExceptionHandler(value = ApiException.class)
    public ResponseEntity<?> apiException(ApiException e){
        log.info("",e);

        ResponseCode responseCode = e.getResponseCode();
        return ResponseEntity.status(responseCode.getCode())
                .body(Api.ERROR(responseCode));
    }
}
