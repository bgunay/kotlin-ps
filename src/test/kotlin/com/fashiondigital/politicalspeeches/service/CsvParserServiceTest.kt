package com.fashiondigital.politicalspeeches.service


import com.fashiondigital.politicalspeeches.TestUtils
import com.fashiondigital.politicalspeeches.TestUtils.CSV_URL_1
import com.fashiondigital.politicalspeeches.TestUtils.CSV_URL_2
import com.fashiondigital.politicalspeeches.TestUtils.INVALID_SPEECHES_DATE
import com.fashiondigital.politicalspeeches.TestUtils.INVALID_SPEECHES_DELIMITER
import com.fashiondigital.politicalspeeches.TestUtils.INVALID_SPEECHES_EMPTY
import com.fashiondigital.politicalspeeches.TestUtils.INVALID_SPEECHES_MINUS_WORDS
import com.fashiondigital.politicalspeeches.TestUtils.SPEAKER_1
import com.fashiondigital.politicalspeeches.TestUtils.SPEAKER_2
import com.fashiondigital.politicalspeeches.TestUtils.SPEAKER_3
import com.fashiondigital.politicalspeeches.config.AppConfig
import com.fashiondigital.politicalspeeches.exception.EvaluationServiceException
import com.fashiondigital.politicalspeeches.model.ErrorCode
import com.fashiondigital.politicalspeeches.model.Speech
import com.fashiondigital.politicalspeeches.service.impl.CsvParserService
import com.fashiondigital.politicalspeeches.util.HttpClient
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.context.annotation.Import
import org.springframework.http.ResponseEntity
import org.springframework.test.annotation.DirtiesContext
import org.springframework.web.client.RestTemplate


@ExtendWith(MockitoExtension::class)
internal class CsvParserServiceTest {

    @Mock
    private lateinit var httpClient: HttpClient

    @Mock
    private lateinit var restTemplate: RestTemplate


    @InjectMocks
    private lateinit var csvParserService: CsvParserService



    @Test
    fun whenParseCsvWithWrongUrl_ThenThrowEvaluationServiceException() {
        Mockito.`when`(httpClient.getHttpCSVResponse(CSV_URL_1)).thenReturn(
            ResponseEntity.ok("")
        )
        val exception =  assertThrows<EvaluationServiceException> {
            csvParserService.parseCSVsByUrls(setOf(CSV_URL_1,CSV_URL_2))
        }
        Assertions.assertTrue(exception.message?.contains(ErrorCode.CSV_EMPTY_BODY_ERROR.value) == true)
        Assertions.assertTrue(exception.message?.contains("Fetched CSV is empty") == true)
    }

    @Test
    fun parseCSVsByUrls_withValidSingleUrl_success() {

        Mockito.`when`(httpClient.getHttpCSVResponse(CSV_URL_1)).thenReturn(
            ResponseEntity.ok(TestUtils.getResourceContent("data/valid-speeches-1.csv"))
        )
        val content = csvParserService.parseCSVsByUrls(setOf(CSV_URL_1))

        assertThat(content).isNotEmpty()
        assertThat(content).hasSize(4)
        content.map { it.speaker }.containsAll(listOf(SPEAKER_1, SPEAKER_2, SPEAKER_3))
    }

    @Test
    fun parseCSVsByUrls_withWrongDelimiter_failed() {
        Mockito.`when`(httpClient.getHttpCSVResponse(CSV_URL_1)).thenReturn(
            ResponseEntity.ok(TestUtils.getResourceContent(INVALID_SPEECHES_DELIMITER))
        )
        val exception =  assertThrows<EvaluationServiceException> {
            csvParserService.parseCSVsByUrls(setOf(CSV_URL_1))
        }
        Assertions.assertTrue(exception.message?.contains(ErrorCode.CSV_PARSER_ERROR.value) == true)
        Assertions.assertTrue(exception.message?.contains("Failed to parse csv file") == true)
    }

    @Test
    fun parseCSVsByUrls_withEmptyCsv_failed() {
        Mockito.`when`(httpClient.getHttpCSVResponse(CSV_URL_1)).thenReturn(
            ResponseEntity.ok(TestUtils.getResourceContent(INVALID_SPEECHES_EMPTY))
        )
        val exception =  assertThrows<EvaluationServiceException> {
            csvParserService.parseCSVsByUrls(setOf(CSV_URL_1))
        }
        Assertions.assertTrue(exception.message?.contains(ErrorCode.CSV_EMPTY_BODY_ERROR.value) == true)
        Assertions.assertTrue(exception.message?.contains("Fetched CSV is empty") == true)
    }

    @Test
    fun parseCSVsByUrls_withWrongDate_failed() {
        Mockito.`when`(httpClient.getHttpCSVResponse(CSV_URL_1)).thenReturn(
            ResponseEntity.ok(TestUtils.getResourceContent(INVALID_SPEECHES_DATE))
        )
        val exception =  assertThrows<EvaluationServiceException> {
            csvParserService.parseCSVsByUrls(setOf(CSV_URL_1))
        }
        Assertions.assertTrue(exception.message?.contains(ErrorCode.CSV_PARSER_ERROR.value) == true)
        Assertions.assertTrue(exception.message?.contains("Failed to parse csv file") == true)

        Assertions.assertTrue(exception.cause?.message?.contains(" could not be parsed at index") == true)
    }

    @Test
    fun parseCSVsByUrls_withMinusNumber_failed() {
        Mockito.`when`(httpClient.getHttpCSVResponse(CSV_URL_1)).thenReturn(
            ResponseEntity.ok(TestUtils.getResourceContent(INVALID_SPEECHES_MINUS_WORDS))
        )
        val exception =  assertThrows<EvaluationServiceException> {
            csvParserService.parseCSVsByUrls(setOf(CSV_URL_1))
        }
        Assertions.assertTrue(exception.message?.contains(ErrorCode.CSV_PARSER_ERROR.value) == true)
        Assertions.assertTrue(exception.message?.contains("Failed to parse csv file") == true)

    }
}