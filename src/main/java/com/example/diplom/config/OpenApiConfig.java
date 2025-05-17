package com.example.diplom.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("Diplom API")
                        .version("1.0.0")
                        .description("API для дипломного проекта")
                        .contact(new Contact()
                                .name("Ваша команда")
                                .email("team@example.com")
                        )
                );
    }
}