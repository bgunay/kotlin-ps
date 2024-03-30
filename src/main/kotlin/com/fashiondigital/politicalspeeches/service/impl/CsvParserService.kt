package com.fashiondigital.politicalspeeches.service.impl

import com.fashiondigital.politicalspeeches.exception.EvaluationServiceException
import com.fashiondigital.politicalspeeches.model.ErrorCode
import com.fashiondigital.politicalspeeches.model.SpeakerStats
import com.fashiondigital.politicalspeeches.model.Speech
import com.fashiondigital.politicalspeeches.model.SpeechHeader
import com.fashiondigital.politicalspeeches.service.ICsvParserService
import com.fashiondigital.politicalspeeches.util.CSVUtil
import org.apache.commons.csv.CSVParser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


@Service
class CsvParserService : ICsvParserService {

    @Autowired
    private lateinit var restTemplate: RestTemplate

    @Value("\${speech.target-year}")
    private val targetYear = 0

    @Value("\${speech.security-topic}")
    private val securityTopic: String? = null

    companion object {
        private val DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd").withLocale(Locale.ENGLISH)
    }

    //return <Speaker, Stats>
    override fun parseCSVsByUrls(urls: Set<String>): Map<String, SpeakerStats> {
        val speakerMap: MutableMap<String, SpeakerStats> = HashMap()
        urls.forEach {
            val response: ResponseEntity<String?> = getHttpCSVResponse(it)
            if (response.hasBody())
                parseCSV(response.body!!, speakerMap)
            else
                throw EvaluationServiceException(ErrorCode.CSV_EMPTY_BODY_ERROR)
        }
        return speakerMap
    }

    private fun getHttpCSVResponse(url: String): ResponseEntity<String?> {
        val response: ResponseEntity<String?>
        try {
            response = restTemplate.exchange(url, HttpMethod.GET, null, String::class.java)
        } catch (ex: RestClientException) {
            throw EvaluationServiceException(ErrorCode.URL_READER_ERROR, ex)
        }
        return response
    }


    //TODO: assumed there isn't any duplication on csv files.
    private fun parseCSV(csvData: String, speakerMap: MutableMap<String, SpeakerStats>) {
        val csvFormat = CSVUtil.setCVSFormat()
        try {
            val csvParser = CSVParser.parse(csvData.byteInputStream(), StandardCharsets.UTF_8, csvFormat)
            val records = csvParser.records
            for (record in records) {
                val speaker = record.get("Speaker")
                val topic = record.get("Topic")
                val date = LocalDate.parse(record.get(SpeechHeader.DATE.value), DATE_TIME_FORMATTER)
                val words = record.get("Words").toInt()
                val speech = Speech(speaker, topic, date, words)
                if (!StringUtils.hasText(speech.speaker) || !StringUtils.hasText(speech.topic)) {
                    throw EvaluationServiceException(ErrorCode.CSV_PARSER_ERROR)
                }
                val speakerStats = SpeakerStats(
                    targetYearSpeeches = if (speech.isTargetYear(targetYear)) 1 else 0, //increment 1
                    securitySpeeches = if (speech.isSecurityTopic(securityTopic!!)) 1 else 0, //increment 1
                    overallWords = speech.words
                )
                speakerMap[speech.speaker] = speakerStats.merge(speakerMap[speech.speaker])
            }
        } catch (ex: Exception) {
            throw EvaluationServiceException(ErrorCode.CSV_PARSER_ERROR, ex)
        }
    }

}

