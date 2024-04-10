package com.fashiondigital.politicalspeeches.util

import com.fashiondigital.politicalspeeches.exception.EvaluationServiceException
import com.fashiondigital.politicalspeeches.model.ErrorCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.awaitBody


@Component
class HttpClient(private val webClient: WebClient) {

    suspend fun getHttpCSVResponse(url: String): String = withContext(Dispatchers.IO) {
        try {
            val response = webClient.get().uri(url)
                .retrieve().awaitBody<String>()
            return@withContext response
        } catch (ex: WebClientResponseException) {
            throw EvaluationServiceException(ErrorCode.URL_READER_ERROR, ex)
        } catch (ex: java.util.NoSuchElementException) {
            throw EvaluationServiceException(ErrorCode.CSV_EMPTY_BODY_ERROR, ex)
        }
    }
}
