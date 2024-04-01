package com.fashiondigital.politicalspeeches.service.impl

import com.fashiondigital.politicalspeeches.exception.CsvParsingException
import com.fashiondigital.politicalspeeches.exception.EvaluationServiceException
import com.fashiondigital.politicalspeeches.model.Speech
import com.fashiondigital.politicalspeeches.model.SpeechHeader
import com.fashiondigital.politicalspeeches.service.ICsvParserService
import com.fashiondigital.politicalspeeches.util.CSVUtil
import com.fashiondigital.politicalspeeches.util.HttpClient
import com.fashiondigital.politicalspeeches.validation.ValidationUtil
import org.apache.commons.csv.CSVParser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


@Service
class CsvParserService(@Autowired val httpClient: HttpClient) : ICsvParserService {

    private val log: Logger = LoggerFactory.getLogger(CsvParserService::class.java)

    companion object {
        val DATE_TIME_FORMATTER: DateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd").withLocale(Locale.ENGLISH)
    }


    //return <Speaker>
    override fun parseCSVsByUrls(urls: Set<String>): List<Speech> {
        log.info("parsing ${urls.size} urls started")
        val speeches = mutableListOf<Speech>()
        urls.forEach {
            val response: ResponseEntity<String?> = httpClient.getHttpCSVResponse(it)
            ValidationUtil.checkCsvValid(response)
            speeches.addAll(parseCSV(response.body!!))
        }
        return speeches
    }


    //TODO: assumed there isn't any duplication on csv files.
    fun parseCSV(csvData: String?): List<Speech> {
        log.info("CSV content parsing started")
        val csvFormat = CSVUtil.setCVSFormat()
        val csvParser = CSVParser.parse(csvData!!.byteInputStream(), StandardCharsets.UTF_8, csvFormat)
        val records = csvParser.records
        try {
            val speeches = records.map {
                val speech = Speech(
                    speaker = it.get(SpeechHeader.SPEAKER.value),
                    topic = it.get(SpeechHeader.TOPIC.value),
                    date = LocalDate.parse(it.get(SpeechHeader.DATE.value), DATE_TIME_FORMATTER),
                    wordCount = it.get(SpeechHeader.WORDS.value).toInt()
                )
                ValidationUtil.validateSpeech(speech)
                speech
            }.toList()
            return speeches
        } catch (ex: Exception) {
            when (ex) {
                is CsvParsingException -> {
                    log.error(ex.message, ex.cause)
                    throw ex
                }
                else -> {
                    log.error(ex.message, ex.cause)
                    throw EvaluationServiceException(ex.message)
                }
            }
        }
    }
}

