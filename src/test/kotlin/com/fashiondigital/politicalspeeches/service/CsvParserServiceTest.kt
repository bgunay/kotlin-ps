package com.fashiondigital.politicalspeeches.service

import com.fashiondigital.politicalspeeches.TestUtils
import com.fashiondigital.politicalspeeches.config.AppConfig
import com.fashiondigital.politicalspeeches.exception.EvaluationServiceException
import com.fashiondigital.politicalspeeches.model.ErrorCode
import com.fashiondigital.politicalspeeches.model.SpeakerStats
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers
import org.springframework.test.web.client.response.MockRestResponseCreators
import org.springframework.web.client.RestTemplate


@RestClientTest(CsvParserService::class)
@Import(AppConfig::class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
internal class CsvParserServiceTest {
    @Autowired
    private lateinit var restTemplate: RestTemplate

    @Autowired
    private lateinit var csvParserService: CsvParserService

    private lateinit var mockServer: MockRestServiceServer

    companion object {
        private const val CSV_URL_1 = "https://csv1.com/example.csv"
        private const val CSV_URL_2 = "https://csv2.com/example.csv"
        private const val VALID_SPEECHES_1 = "data/valid-speeches-1.csv"
        private const val VALID_SPEECHES_2 = "data/valid-speeches-2.csv"
        private const val INVALID_SPEECHES_DELIMITER = "data/invalid-speeches-delimiter.csv"
        private const val INVALID_SPEECHES_EMPTY = "data/invalid-speeches-empty.csv"
        private const val INVALID_SPEECHES_DATE = "data/invalid-speeches-date.csv"
        private const val INVALID_SPEECHES_MINUS_WORDS = "data/invalid-speeches-minus.csv"
        private const val SPEAKER_1 = "Alexander Abel"
        private const val SPEAKER_2 = "Bernhard Belling"
        private const val SPEAKER_3 = "Caesare Collins"
    }

    @BeforeEach
    fun init() {
        mockServer = MockRestServiceServer.createServer(restTemplate)
    }

    @Test
    @Throws(Exception::class)
    fun parseCSVsByUrls_withValidSingleUrl_success() {
        mockUrlCall(CSV_URL_1, VALID_SPEECHES_1)
        val speakerStatsMap: Map<String, SpeakerStats> = csvParserService.parseCSVsByUrls(setOf(CSV_URL_1))
        assertThat(speakerStatsMap).isNotEmpty()
        assertThat(speakerStatsMap).hasSize(3)
        assertThat(speakerStatsMap).containsKeys(SPEAKER_1, SPEAKER_2, SPEAKER_3)
                .hasEntrySatisfying(SPEAKER_1) { v -> assertThat(v.securitySpeeches).isEqualTo(1) }
                .hasEntrySatisfying(SPEAKER_2) { v -> assertThat(v.overallWords).isEqualTo(1210) }
                .hasEntrySatisfying(SPEAKER_3) { v -> assertThat(v.targetYearSpeeches).isEqualTo(0) }
    }

    @Test
    @Throws(Exception::class)
    fun parseCSVsByUrls_withValidMultiUrls_success() {
        mockUrlCall(CSV_URL_1, VALID_SPEECHES_1)
        mockUrlCall(CSV_URL_2, VALID_SPEECHES_2)
        val speakerStatsMap: Map<String, SpeakerStats> = csvParserService.parseCSVsByUrls(setOf(CSV_URL_1, CSV_URL_2))
        assertThat(speakerStatsMap).isNotNull
        assertThat(speakerStatsMap).hasSize(3)
        assertThat(speakerStatsMap).containsKeys(SPEAKER_1, SPEAKER_2, SPEAKER_3)
                .hasEntrySatisfying(SPEAKER_1) { v -> assertThat(v.overallWords).isEqualTo(6110) }
                .hasEntrySatisfying(SPEAKER_2) { v -> assertThat(v.securitySpeeches).isEqualTo(0) }
                .hasEntrySatisfying(SPEAKER_3) { v -> assertThat(v.targetYearSpeeches).isEqualTo(2) }
    }

    @Test
    @Throws(Exception::class)
    fun parseCSVsByUrls_withWrongDelimiter_failed() {
        mockUrlCall(CSV_URL_1, INVALID_SPEECHES_DELIMITER)
        val ex: Exception = org.junit.jupiter.api.Assertions.assertThrows(EvaluationServiceException::class.java) { csvParserService!!.parseCSVsByUrls(setOf(CSV_URL_1)) }
        assertThat(ex.message).isEqualTo(ErrorCode.CSV_PARSER_ERROR.value)
    }

    @Test
    @Throws(Exception::class)
    fun parseCSVsByUrls_withEmptyCsv_failed() {
        mockUrlCall(CSV_URL_1, INVALID_SPEECHES_EMPTY)
        val ex: EvaluationServiceException = org.junit.jupiter.api.Assertions.assertThrows(EvaluationServiceException::class.java, Executable { csvParserService!!.parseCSVsByUrls(setOf(CSV_URL_1)) })
        assertThat(ex.message).isEqualTo(ErrorCode.CSV_PARSER_ERROR.value)
    }

    @Test
    @Throws(Exception::class)
    fun parseCSVsByUrls_withWrongDate_failed() {
        mockUrlCall(CSV_URL_1, INVALID_SPEECHES_DATE)
        val ex: Exception = org.junit.jupiter.api.Assertions.assertThrows(EvaluationServiceException::class.java) { csvParserService!!.parseCSVsByUrls(setOf(CSV_URL_1)) }
        assertThat(ex.message).isEqualTo(ErrorCode.CSV_PARSER_ERROR.value)
    }

    @Test
    @Throws(Exception::class)
    fun parseCSVsByUrls_withMinusNumber_failed() {
        mockUrlCall(CSV_URL_1, INVALID_SPEECHES_MINUS_WORDS)
        val ex: Exception = org.junit.jupiter.api.Assertions.assertThrows(EvaluationServiceException::class.java) { csvParserService!!.parseCSVsByUrls(setOf(CSV_URL_1)) }
        assertThat(ex.message).isEqualTo(ErrorCode.CSV_PARSER_ERROR.value)
    }

    @Test
    fun parseCSVsByUrls_withUrlError_failed() {
        mockServer.expect(MockRestRequestMatchers.requestTo(CSV_URL_1))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withBadRequest())
        val ex: Exception = org.junit.jupiter.api.Assertions.assertThrows(EvaluationServiceException::class.java) { csvParserService!!.parseCSVsByUrls(setOf(CSV_URL_1)) }
        assertThat(ex.message).isEqualTo(ErrorCode.URL_READER_ERROR.value)
    }

    @Throws(Exception::class)
    private fun mockUrlCall(url: String, csvPath: String) {
        val content: String = TestUtils.getResourceContent(csvPath)
        mockServer.expect(MockRestRequestMatchers.requestTo(url))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withSuccess(content, MediaType.TEXT_PLAIN))
    }
}