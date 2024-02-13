package com.blind.dating.aop;

import com.blind.dating.common.Api;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
public class LogAspect {
    @Pointcut("execution(* com.blind.dating.controller.*.*(..))")
    private void controllerLog(){}


    @AfterReturning(value = "(controllerLog())", returning = "responseDto")
    public void afterReturningLogin(JoinPoint joinPoint, Object responseDto) {
        // 응답 데이터 처리
         Api<?> result = (Api<?>) ((ResponseEntity<?>) responseDto).getBody();
         String message = result.getMessage();
        // HttpServletRequest 객체 가져오기
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        // URL 가져오기
        String url = request.getRequestURL().toString();
        String method = request.getMethod();

        log.info("url: {}, method: {}, status: {}, message: {}",url,method,"success",message);
    }
}
