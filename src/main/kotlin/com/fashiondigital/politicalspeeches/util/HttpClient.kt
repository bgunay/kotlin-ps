package com.fashiondigital.politicalspeeches.util

import com.fashiondigital.politicalspeeches.exception.EvaluationServiceException
import com.fashiondigital.politicalspeeches.model.ErrorCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.toEntity


@Component
class HttpClient(private val webClient: WebClient) {

    suspend fun getHttpCSVResponse(url: String): ResponseEntity<String>? = withContext(Dispatchers.IO) {
        try {
            val response = webClient.get().uri(url).retrieve().toEntity<String>().block()
            return@withContext response
        } catch (ex: WebClientResponseException) {
            throw EvaluationServiceException(ErrorCode.URL_READER_ERROR, ex)
        }
    }


}
