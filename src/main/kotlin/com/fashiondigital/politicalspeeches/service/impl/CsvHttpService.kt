package com.fashiondigital.politicalspeeches.service.impl

import com.fashiondigital.politicalspeeches.exception.CsvParsingException
import com.fashiondigital.politicalspeeches.model.ErrorCode
import com.fashiondigital.politicalspeeches.model.constants.Constants
import com.fashiondigital.politicalspeeches.service.ICsvHttpService
import com.fashiondigital.politicalspeeches.util.HttpClient
import com.fashiondigital.politicalspeeches.validation.ValidationUtil
import kotlinx.coroutines.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service


@Service
class CsvHttpService(@Autowired val httpClient: HttpClient) : ICsvHttpService {

    private val log: Logger = LoggerFactory.getLogger(CsvHttpService::class.java)


    //return <Speaker>
    override fun parseUrlsAndFetchCsvData(urls: Set<String>): List<String?> {
        log.info("parsing ${urls.size} urls started")
        val csvContents: List<ResponseEntity<String?>>
        runBlocking {
            try {
                csvContents = withTimeout(Constants.FETCH_CSV_TIMEOUT) {
                    urls.map { url ->
                        async { httpClient.getHttpCSVResponse(url) }
                    }.awaitAll()
                }
                ValidationUtil.checkCsvResponsesValid(csvContents)

            } catch (ex: TimeoutCancellationException) {
                log.error(ErrorCode.FETCH_CSV_TIMEOUT.value)
                throw CsvParsingException(ErrorCode.FETCH_CSV_TIMEOUT)
            }
        }
        return csvContents.map { it.body }
    }

}
