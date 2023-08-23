package com.blind.dating.exception;

import com.blind.dating.dto.response.ResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalException {

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<ResponseDto> runtimeException(Exception e){

        return ResponseEntity.<ResponseDto>badRequest()
                .body(ResponseDto.builder()
                        .status("Bad request")
                        .message(e.getMessage())
                        .build());
    }
}
