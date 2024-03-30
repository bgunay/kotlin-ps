package com.fashiondigital.politicalspeeches.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {
    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .components(Components())
            .info(
                Info()
                    .title("Politicians Speeches API")
                    .contact(Contact().name("burhan").url("bgunay@github.com"))
                    .description("This is the API documentation for Politicians Speeches.")
            )
    }
}