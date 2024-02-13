package com.blind.dating.common;

import com.blind.dating.common.code.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Api<T> {

    private int code;
    private String status;
    private String message;

    private T data;

    public static <T> Api<T> OK(ResponseCode responseCode, T data){
        return new Api<T>(
                responseCode.getCode(),
                responseCode.getStatus(),
                responseCode.getMessage(),
                data);
    }

    public static <T> Api<T> OK(ResponseCode responseCode){
        return new Api<T>(
                responseCode.getCode(),
                responseCode.getStatus(),
                responseCode.getMessage(),
                null);
    }

    public static <T> Api<T> ERROR(ResponseCode code) {
        return new Api<T>(
            code.getCode(), code.getStatus(), code.getMessage(), null
        );
    }

}
