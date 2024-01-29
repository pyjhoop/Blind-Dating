package com.blind.dating.exception;

import com.blind.dating.dto.response.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalException {

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<ResponseDto> runtimeException(Exception e){

        log.error(e+"");

        return ResponseEntity.<ResponseDto>badRequest()
                .body(ResponseDto.builder()
                        .status("Bad request")
                        .message(e.getMessage())
                        .build());
    }
}
