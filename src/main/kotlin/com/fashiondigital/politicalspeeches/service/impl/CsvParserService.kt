package com.fashiondigital.politicalspeeches.service.impl

import com.fashiondigital.politicalspeeches.exception.CsvParsingException
import com.fashiondigital.politicalspeeches.exception.EvaluationServiceException
import com.fashiondigital.politicalspeeches.model.Speech
import com.fashiondigital.politicalspeeches.model.SpeechHeader
import com.fashiondigital.politicalspeeches.service.ICsvParserService
import com.fashiondigital.politicalspeeches.util.CSVUtil
import com.fashiondigital.politicalspeeches.validation.ValidationUtil
import org.apache.commons.csv.CSVParser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


@Service
class CsvParserService() : ICsvParserService {

    companion object {
        val DATE_TIME_FORMATTER: DateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd").withLocale(Locale.ENGLISH)
        private val log: Logger = LoggerFactory.getLogger(CsvParserService::class.java)

    }


    //TODO: assumed there isn't any duplication on csv files.
    override fun parseCSV(csvData: List<String?>): List<Speech> {
        log.info("CSV content parsing started")
        val allSpeeches = mutableListOf<Speech>()
        try {
            csvData.forEach { csvFile ->
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
                    log.error(ex.message, ex)
                    throw ex
                }

                else -> {
                    log.error(ex.message, ex)
                    throw EvaluationServiceException(ex.message)
                }
            }
        }
    }
}

