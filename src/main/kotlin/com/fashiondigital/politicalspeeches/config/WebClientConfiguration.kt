package com.fashiondigital.politicalspeeches.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfiguration {

    @Value("\${csv.server.address}")
    private val url: String? = null

    @Bean
    fun webClient() = WebClient.builder().baseUrl(url!!).build()
}