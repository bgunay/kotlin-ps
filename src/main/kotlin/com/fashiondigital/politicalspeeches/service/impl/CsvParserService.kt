package com.fashiondigital.politicalspeeches.service.impl

import com.fashiondigital.politicalspeeches.exception.CsvParsingException
import com.fashiondigital.politicalspeeches.exception.EvaluationServiceException
import com.fashiondigital.politicalspeeches.model.ErrorCode
import com.fashiondigital.politicalspeeches.model.Speech
import com.fashiondigital.politicalspeeches.service.ICsvParserService
import com.fashiondigital.politicalspeeches.util.HttpClient
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.format.DateTimeParseException


@Service
class CsvParserService(@Autowired val httpClient: HttpClient) : ICsvParserService {

    //return <Speaker, Stats>
    override suspend fun parseCSVsByUrls(urls: List<String>): List<Speech> {
        val speecs:  List<Speech>

        coroutineScope {
            val csvContents = urls
                .map { url -> async { httpClient.downloadCsv(url) } }
                .awaitAll()

              speecs = csvContents.flatMap { csvContent ->
                parseCSV(csvContent)
            }
        }
        return speecs
    }

    fun parseCSV(csvData: String): List<Speech> {
        try {
            val speech = csvReader().readAllWithHeader(csvData).map { row ->
                Speech(
                    speaker = row["Speaker"] ?: throw CsvParsingException(ErrorCode.SPEAKER_MISSING_ERROR),
                    topic = row["Topic"] ?: throw CsvParsingException(ErrorCode.TOPIC_MISSING_ERROR),
                    date = LocalDate.parse(row["Date"]),
                    wordCount = row["Words"]?.toInt() ?: throw CsvParsingException(ErrorCode.WORD_COUNT_ERROR),
                )
            }
            return speech
        } catch (ex: Exception) {
            throw EvaluationServiceException(ErrorCode.CSV_PARSER_ERROR, ex)
        } catch (ex: DateTimeParseException) {
            throw CsvParsingException(ErrorCode.INVALID_DATE_FORMAT, ex)
        } catch (ex: NumberFormatException) {
            throw CsvParsingException(ErrorCode.INVALID_NUMBER_FORMAT, ex)
        }
    }

}