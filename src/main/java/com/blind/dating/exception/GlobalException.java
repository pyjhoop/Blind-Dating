package com.blind.dating.exception;

import com.blind.dating.dto.response.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

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

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ResponseDto> handleValidationException(MethodArgumentNotValidException  e) {
        BindingResult bindingResult = e.getBindingResult();
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(ResponseDto.builder()
                .status("BAD REQUEST")
                .message("회원가입에 실패했습니다.")
                .data(errors)
                .build());
    }
}
