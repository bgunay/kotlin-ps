package com.fashiondigital.politicalspeeches.util

import com.fashiondigital.politicalspeeches.exception.EvaluationServiceException
import com.fashiondigital.politicalspeeches.model.ErrorCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate


@Component
class HttpClient {

    @Autowired
    lateinit var restTemplate: RestTemplate

    companion object {
        private val log by LoggerDelegate()
    }

    suspend fun getHttpCSVResponse(url: String): ResponseEntity<String?> = withContext(Dispatchers.IO) {
        val response: ResponseEntity<String?>
        try {
            response = restTemplate.exchange(url, HttpMethod.GET, null, String::class.java)
        } catch (ex: RestClientException) {
            log.error(ErrorCode.URL_READER_ERROR.value, ex)
            throw EvaluationServiceException(ErrorCode.URL_READER_ERROR, ex)
        }

        return@withContext response
    }


}
