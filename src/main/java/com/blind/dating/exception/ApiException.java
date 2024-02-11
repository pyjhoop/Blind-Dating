package com.blind.dating.exception;

import com.blind.dating.common.code.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class ApiException extends RuntimeException{
    private final ResponseCode responseCode;

    public ApiException(ResponseCode responseCode) {
        this.responseCode = responseCode;
    }
}
