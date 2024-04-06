package com.fashiondigital.politicalspeeches.service.impl

import com.fashiondigital.politicalspeeches.exception.CsvPHttpException
import com.fashiondigital.politicalspeeches.model.ErrorCode
import com.fashiondigital.politicalspeeches.service.ICsvHttpService
import com.fashiondigital.politicalspeeches.util.HttpClient
import com.fashiondigital.politicalspeeches.util.LoggerDelegate
import com.fashiondigital.politicalspeeches.validation.ValidationUtil
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withTimeout
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

    //return <Speaker>
    override suspend fun parseUrlsAndFetchCsvData(urls: Set<String>): List<String?> {
        log.info("parsing ${urls.size} urls started")
        val csvContents: List<ResponseEntity<String>?>
        try {
            csvContents = withTimeout(fetchCsvTimeout) {
                urls.map { url ->
                    async {
                        val httpCSVResponse = httpClient.getHttpCSVResponse(url)
                        ValidationUtil.checkCsvResponseValid(httpCSVResponse)
                        log.info("response fetched for $url")
                        httpCSVResponse
                    }
                }.awaitAll()
            }
        } catch (ex: TimeoutCancellationException) {
            log.error(ErrorCode.FETCH_CSV_TIMEOUT.value, ex)
            throw CsvPHttpException(ErrorCode.FETCH_CSV_TIMEOUT)
        }
        return csvContents.map { it?.body }
    }

}

