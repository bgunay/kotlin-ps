package com.fashiondigital.politicalspeeches.service.impl

import com.fashiondigital.politicalspeeches.exception.CsvParsingException
import com.fashiondigital.politicalspeeches.exception.EvaluationServiceException
import com.fashiondigital.politicalspeeches.model.Speech
import com.fashiondigital.politicalspeeches.model.SpeechHeader
import com.fashiondigital.politicalspeeches.service.ICsvParserService
import com.fashiondigital.politicalspeeches.util.CSVUtil
import com.fashiondigital.politicalspeeches.util.HttpClient
import com.fashiondigital.politicalspeeches.validation.ValidationUtil
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import org.apache.commons.csv.CSVParser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
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
        var speeches = listOf<Speech>()
        runBlocking {
            coroutineScope {
                log.info("parsing ${urls.size} urls started")
                val csvContents = urls.map { url ->
                    async { httpClient.getHttpCSVResponse(url) }
                }.awaitAll()

                ValidationUtil.checkCsvResponsesValid(csvContents)
                speeches = parseCSV(csvContents.map { it.body})
            }
        }
        return speeches
    }

    //TODO: assumed there isn't any duplication on csv files.
      fun parseCSV(csvData: List<String?>): List<Speech> {
        log.info("CSV content parsing started")
        val allSpeeches = mutableListOf<Speech>()
        try {
            csvData.forEach {csvFile ->
                val csvFormat = CSVUtil.setCVSFormat()
                val csvParser = CSVParser.parse(csvFile!!.byteInputStream(), StandardCharsets.UTF_8, csvFormat)
                val records = csvParser.records
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
                allSpeeches.addAll(speeches)
            }
            return allSpeeches
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

