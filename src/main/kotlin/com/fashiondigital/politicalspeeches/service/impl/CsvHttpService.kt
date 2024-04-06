package com.fashiondigital.politicalspeeches.service.impl

import com.fashiondigital.politicalspeeches.config.mapAsync
import com.fashiondigital.politicalspeeches.exception.CsvPHttpException
import com.fashiondigital.politicalspeeches.model.ErrorCode
import com.fashiondigital.politicalspeeches.service.ICsvHttpService
import com.fashiondigital.politicalspeeches.util.HttpClient
import com.fashiondigital.politicalspeeches.util.LoggerDelegate
import com.fashiondigital.politicalspeeches.validation.ValidationUtil
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service


@Service
class CsvHttpService(private val httpClient: HttpClient) : ICsvHttpService {

    companion object {
        private val log by LoggerDelegate()
    }

    @Value("\${fetch.csv.timeout}")
    private val fetchCsvTimeout: Long = 0

    override suspend fun parseUrlsAndFetchCsvData(urls: Set<String>): List<String?> {
        log.info("parsing ${urls.size} urls started")
        val csvContents: List<ResponseEntity<String>?>
        try {
            csvContents = urls.mapAsync(::transformAndCheck,fetchCsvTimeout)
        } catch (ex: TimeoutCancellationException) {
            log.error(ErrorCode.FETCH_CSV_TIMEOUT.value, ex)
            throw CsvPHttpException(ErrorCode.FETCH_CSV_TIMEOUT, ex)
        }
        return csvContents.map { it?.body }
    }

    public suspend fun transformAndCheck(url: String): ResponseEntity<String>? {
        val httpCSVResponse = httpClient.getHttpCSVResponse(url)
        ValidationUtil.checkCsvResponseValid(httpCSVResponse)
        log.info("response fetched for $url")
        return httpCSVResponse
    }

}

