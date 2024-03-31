package com.fashiondigital.politicalspeeches.service.impl

import com.fashiondigital.politicalspeeches.exception.CsvParsingException
import com.fashiondigital.politicalspeeches.exception.EvaluationServiceException
import com.fashiondigital.politicalspeeches.model.ErrorCode
import com.fashiondigital.politicalspeeches.model.Speech
import com.fashiondigital.politicalspeeches.model.SpeechHeader
import com.fashiondigital.politicalspeeches.service.ICsvParserService
import com.fashiondigital.politicalspeeches.util.CSVUtil
import com.fashiondigital.politicalspeeches.util.HttpClient
import org.apache.commons.csv.CSVParser
import org.apache.logging.log4j.util.Strings
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


@Service
class CsvParserService(@Autowired val httpClient: HttpClient) : ICsvParserService {

    companion object {
        val DATE_TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd").withLocale(Locale.ENGLISH)
    }


    //return <Speaker, Stats>
    override fun parseCSVsByUrls(urls: Set<String>): List<Speech> {
        if(urls.isEmpty())
            throw EvaluationServiceException(ErrorCode.URL_READER_ERROR)

        val stats = mutableListOf<Speech>()
        urls.forEach {
            val response: ResponseEntity<String?> = httpClient.getHttpCSVResponse(it)
            if (response.hasBody() && !Strings.isEmpty(response.body)    ) {
                stats.addAll(parseCSV(response.body!!))
            } else
                throw EvaluationServiceException(ErrorCode.CSV_EMPTY_BODY_ERROR)
        }
        return stats
    }


    //TODO: assumed there isn't any duplication on csv files.
    fun parseCSV(csvData: String?): List<Speech> {
        val csvFormat = CSVUtil.setCVSFormat()
        val csvParser = CSVParser.parse(csvData!!.byteInputStream(), StandardCharsets.UTF_8, csvFormat)
        val records = csvParser.records
        return try {
            records.map {
                Speech(
                    speaker = it.get("Speaker") ?: throw CsvParsingException("Speaker is missing"),
                    topic = it.get("Topic") ?: throw CsvParsingException("Topic is missing"),
                    date = LocalDate.parse(it.get(SpeechHeader.DATE.value), DATE_TIME_FORMATTER),
                    wordCount = it.get("Words").toInt() ?: throw CsvParsingException("Words count is missing")
                )
            }
        } catch (ex: Exception) {
            throw EvaluationServiceException(ErrorCode.CSV_PARSER_ERROR, ex)
        }
    }

}

