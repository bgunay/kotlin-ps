package com.fashiondigital.politicalspeeches.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.RestTemplate

@Configuration
class AppConfig {
    @Bean
    fun restTemplate(): RestTemplate {
        val restTemplate = RestTemplate()
        val requestFactory = HttpComponentsClientHttpRequestFactory()
        requestFactory.setConnectTimeout(5000)
        restTemplate.requestFactory = requestFactory;

        return restTemplate;
    }


}