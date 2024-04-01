package com.fashiondigital.politicalspeeches.service


import com.fashiondigital.politicalspeeches.TestUtils
import com.fashiondigital.politicalspeeches.TestUtils.CSV_URL_1
import com.fashiondigital.politicalspeeches.TestUtils.CSV_URL_2
import com.fashiondigital.politicalspeeches.TestUtils.INVALID_COLUMN
import com.fashiondigital.politicalspeeches.TestUtils.INVALID_SPEECHES_DATE
import com.fashiondigital.politicalspeeches.TestUtils.INVALID_SPEECHES_DELIMITER
import com.fashiondigital.politicalspeeches.TestUtils.INVALID_SPEECHES_EMPTY
import com.fashiondigital.politicalspeeches.TestUtils.INVALID_SPEECHES_MINUS_WORDS
import com.fashiondigital.politicalspeeches.TestUtils.INVALID_SPEECHES_MISSING_TOPIC
import com.fashiondigital.politicalspeeches.TestUtils.SPEAKER_1
import com.fashiondigital.politicalspeeches.TestUtils.SPEAKER_2
import com.fashiondigital.politicalspeeches.TestUtils.SPEAKER_3
import com.fashiondigital.politicalspeeches.TestUtils.VALID_SPEECHES_1
import com.fashiondigital.politicalspeeches.TestUtils.VALID_SPEECHES_2
import com.fashiondigital.politicalspeeches.exception.CsvParsingException
import com.fashiondigital.politicalspeeches.exception.EvaluationServiceException
import com.fashiondigital.politicalspeeches.model.ErrorCode
import com.fashiondigital.politicalspeeches.service.impl.CsvParserService
import com.fashiondigital.politicalspeeches.util.HttpClient
import io.mockk.coEvery
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.http.ResponseEntity


internal class CsvParserServiceTest {

    private val httpClientMock = mockk<HttpClient>(relaxed = true)

    private var csvParserService = CsvParserService(httpClientMock)

    @Test
    fun parseCSVsByUrls_withValidSingleUrl_success() {

        val ok = ResponseEntity.ok(TestUtils.getResourceContent(arrayOf(VALID_SPEECHES_1, VALID_SPEECHES_2).random()))
        coEvery { httpClientMock.getHttpCSVResponse(CSV_URL_1) } returns ok

        val content = csvParserService.parseCSVsByUrls(setOf(CSV_URL_1))

        assertThat(content).isNotEmpty()
        assertThat(content).hasSize(4)
        content.map { it.speaker }.containsAll(listOf(SPEAKER_1, SPEAKER_2, SPEAKER_3))
    }


    @Test
    fun parseCSVsByUrls_withEmptyContent_failed() {
        coEvery { httpClientMock.getHttpCSVResponse(CSV_URL_1) } returns ResponseEntity.ok("")

        val exception = assertThrows<CsvParsingException> {
            csvParserService.parseCSVsByUrls(setOf(CSV_URL_1, CSV_URL_2))
        }
        Assertions.assertTrue(exception.message?.contains(ErrorCode.CSV_EMPTY_BODY_ERROR.value) == true)
    }


    @Test
    fun parseCSVsByUrls_withWrongDelimiter_failed() {

        val ok = ResponseEntity.ok(TestUtils.getResourceContent(INVALID_SPEECHES_DELIMITER))
        coEvery { httpClientMock.getHttpCSVResponse(CSV_URL_1) } returns ok

        val exception = assertThrows<CsvParsingException> {
            csvParserService.parseCSVsByUrls(setOf(CSV_URL_1))
        }
        Assertions.assertTrue(exception.message?.contains(ErrorCode.WRONG_DELIMITER_CSV.value) == true)
    }

    @Test
    fun parseCSVsByUrls_withEmptyCsv_failed() {

        val ok = ResponseEntity.ok(TestUtils.getResourceContent(INVALID_SPEECHES_EMPTY))
        coEvery { httpClientMock.getHttpCSVResponse(CSV_URL_1) } returns ok

        val exception = assertThrows<CsvParsingException> {
            csvParserService.parseCSVsByUrls(setOf(CSV_URL_1))
        }
        Assertions.assertTrue(exception.message?.contains(ErrorCode.CSV_EMPTY_BODY_ERROR.value) == true)
    }

    @Test
    fun parseCSVsByUrls_withWrongDate_failed() {

        val ok = ResponseEntity.ok(TestUtils.getResourceContent(INVALID_SPEECHES_DATE))
        coEvery { httpClientMock.getHttpCSVResponse(CSV_URL_1) } returns ok

        val exception = assertThrows<EvaluationServiceException> {
            csvParserService.parseCSVsByUrls(setOf(CSV_URL_1))
        }
        Assertions.assertTrue(exception.message?.contains("could not be parsed") == true)
    }

    @Test
    fun parseCSVsByUrls_withMinusNumber_failed() {
        val ok = ResponseEntity.ok(TestUtils.getResourceContent(INVALID_SPEECHES_MINUS_WORDS))
        coEvery { httpClientMock.getHttpCSVResponse(CSV_URL_1) } returns ok

        val exception = assertThrows<CsvParsingException> {
            csvParserService.parseCSVsByUrls(setOf(CSV_URL_1))
        }
        Assertions.assertTrue(exception.message?.contains(ErrorCode.MINUS_WORD_ERROR.value) == true)

    }

    @Test
    fun parseCSVsByUrls_withMissingTopic_failed() {

        val ok = ResponseEntity.ok(TestUtils.getResourceContent(INVALID_SPEECHES_MISSING_TOPIC))
        coEvery { httpClientMock.getHttpCSVResponse(CSV_URL_1) } returns ok

        val exception = assertThrows<CsvParsingException> {
            csvParserService.parseCSVsByUrls(setOf(CSV_URL_1))
        }
        Assertions.assertTrue(exception.message?.contains(ErrorCode.TOPIC_MISSING.value) == true)

    }

    @Test
    fun parseCSVsByUrls_Invalid_Column_failed() {

        val ok = ResponseEntity.ok(TestUtils.getResourceContent(INVALID_COLUMN))
        coEvery { httpClientMock.getHttpCSVResponse(CSV_URL_1) } returns ok

        val exception = assertThrows<EvaluationServiceException> {
            csvParserService.parseCSVsByUrls(setOf(CSV_URL_1))
        }
        Assertions.assertTrue(exception.message?.contains("expected one of [Date, Invalid Column, Topic, Words") == true)
    }
}