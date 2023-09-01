package com.blind.dating.security;

import com.blind.dating.dto.response.ResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        System.out.println("여기?");
        try{
            String errorMessage = request.getAttribute("error").toString();
            setResponse(response, errorMessage);
        }catch (NullPointerException e){

        }


    }

    /**
     * 한글 출력을 위해 getWriter() 사용
     */
    private void setResponse(HttpServletResponse response, String errorMessage) throws IOException {
        System.out.println("여기?1");
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        ObjectMapper ob = new ObjectMapper();
        ResponseEntity entity =ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ResponseDto.builder()
                        .status("Unauthorized")
                        .message(errorMessage)
                        .build());

        response.getWriter().println(ob.writeValueAsString(entity));
    }

}