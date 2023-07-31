package com.blind.dating.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public GroupedOpenApi publicApi(){
        return GroupedOpenApi.builder()
                .group("Back-End")
                .pathsToMatch("/api/**")
                .build();
    }

    @Bean
    public OpenAPI springShopOpenApi(){
        return new OpenAPI()
                .info(new Info().title("Blind Dating Back End API")
                        .description("데이팅 웹사이트 API 명세서입니다.")
                        .version("v0.0.1"));
    }

}
