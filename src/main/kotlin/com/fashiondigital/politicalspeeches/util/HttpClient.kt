package com.fashiondigital.politicalspeeches.util

import com.fashiondigital.politicalspeeches.exception.EvaluationServiceException
import com.fashiondigital.politicalspeeches.model.ErrorCode
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate


@Component
class HttpClient {

    @Autowired
    private lateinit var restTemplate: RestTemplate

    fun getHttpCSVResponse(url: String): ResponseEntity<String?> {
        val response: ResponseEntity<String?>
        try {
            response = restTemplate.exchange(url, HttpMethod.GET, null, String::class.java)
        } catch (ex: RestClientException) {
            throw EvaluationServiceException(ErrorCode.URL_READER_ERROR, ex)
        }
        return response
    }


}
