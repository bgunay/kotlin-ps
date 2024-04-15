package com.fashiondigital.politicalspeeches.service.impl

import com.fashiondigital.politicalspeeches.config.mapAsync
import com.fashiondigital.politicalspeeches.exception.CsvPHttpException
import com.fashiondigital.politicalspeeches.model.ErrorCode
import com.fashiondigital.politicalspeeches.service.ICsvHttpService
import com.fashiondigital.politicalspeeches.util.HttpClient
import com.fashiondigital.politicalspeeches.util.LoggerDelegate
import com.fashiondigital.politicalspeeches.validation.ValidationUtil
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.map
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service


@Service
class CsvHttpService(private val httpClient: HttpClient) : ICsvHttpService {

    companion object {
        private val log by LoggerDelegate()
    }

    @Value("\${fetch.csv.timeout}")
    private val fetchCsvTimeout: Long = 0

    override suspend fun fetchCsvData(urls: Set<String>): List<String?> {
        log.info("parsing ${urls.size} urls started")
        val csvContents: List<String>
        try {
            csvContents = urls.mapAsync(
                {
                    val content = fetchContent(it)
                    validateContent(content)
                    content
                },
                fetchCsvTimeout
            )
        } catch (ex: TimeoutCancellationException) {
            log.error(ErrorCode.FETCH_CSV_TIMEOUT.value, ex)
            throw CsvPHttpException(ErrorCode.FETCH_CSV_TIMEOUT, ex)
        }
        return csvContents
    }

    // for buffered version
    override suspend fun fetchCsvData2(urls: Set<String>): List<String?> {
        log.info("parsing ${urls.size} urls started")
        val csvContents = mutableListOf<String>()
        val flow = urls.asFlow()
        flow.buffer(10).map { url ->
            fetchContent(url)
                .also { validateContent(it) }
        }.collect {
            csvContents.add(it)
        }
        return csvContents
    }

    suspend fun fetchContent(url: String): String {
        val httpCSVResponse = httpClient.getHttpCSVResponse(url)
        log.info("response fetched for $url")
        return httpCSVResponse
    }

    private fun validateContent(httpCSVResponse: String) {
        ValidationUtil.checkCsvResponseValid(httpCSVResponse)
    }

}

