package com.fashiondigital.politicalspeeches.controller

import com.fashiondigital.politicalspeeches.TestUtils
import com.fashiondigital.politicalspeeches.TestUtils.VALID_CSV_URL
import com.fashiondigital.politicalspeeches.exception.EvaluationServiceException
import com.fashiondigital.politicalspeeches.model.ErrorCode
import com.fashiondigital.politicalspeeches.model.EvaluationResult
import com.fashiondigital.politicalspeeches.service.ICsvHttpService
import com.fashiondigital.politicalspeeches.service.IEvaluationService
import com.fashiondigital.politicalspeeches.service.impl.CsvHttpService
import com.fashiondigital.politicalspeeches.service.impl.CsvParserService
import com.fashiondigital.politicalspeeches.service.impl.EvaluationService
import io.mockk.*
import kotlinx.coroutines.test.*
import org.hamcrest.Matchers
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

// TODO: Fix GlobalExceptionHandler problem for suspending controller endpoints


@ExtendWith(SpringExtension::class)
@WebMvcTest(EvaluationController::class)
internal class EvaluationControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Value("\${csv.server.address}")
    private lateinit var url: String

    @TestConfiguration
    class ControllerTestConfig {
        @Bean
        fun evaluationService() = mockk<EvaluationService>()

        @Bean
        fun csvParserService() = mockk<CsvParserService>()

        @Bean
        fun csvHttpService() = mockk<CsvHttpService>()
    }

    @Autowired
    private lateinit var csvParserService: CsvParserService

    @Autowired
    private lateinit var evaluationService: IEvaluationService

    @Autowired
    private lateinit var csvHttpService: ICsvHttpService


    companion object {
        private val EVALUATION_RESULT: EvaluationResult = EvaluationResult(
            mostSpeeches = "A",
            mostSecurity = "B",
            leastWordy = "C"
        )
    }

    @Test
    fun evaluate_success() = runTest {
        // Given
        val csvStringContent = listOf("cvsContent")
        coEvery { csvHttpService.parseUrlsAndFetchCsvData(setOf(VALID_CSV_URL)) } coAnswers { csvStringContent }
        every { csvParserService.parseCSV(csvStringContent) } answers { TestUtils.validSpeeches1 }
        every { evaluationService.analyzeSpeeches(TestUtils.validSpeeches1) } answers { EVALUATION_RESULT }

        // When
        val result = mockMvc.perform(get("/evaluate").queryParam("url1", VALID_CSV_URL))

        // Then
        result
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.mostSpeeches", Matchers.`is`("A")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.mostSecurity", Matchers.`is`("B")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.leastWordy", Matchers.`is`("C")))
    }

//    @Test
//    fun evaluate2_success() = runTest {
//        val csvStringContent = listOf("cvsContent")
//        coEvery { csvHttpService.parseUrlsAndFetchCsvData(setOf(VALID_CSV_URL)) } coAnswers { csvStringContent }
//        every { csvParserService.parseCSV(csvStringContent) } answers { TestUtils.validSpeeches1 }
//        every { evaluationService.analyzeSpeeches(TestUtils.validSpeeches1) } answers { EVALUATION_RESULT }
//        mockMvc.perform(get("/evaluate2").queryParam("url1", VALID_CSV_URL))
//            .andExpect(status().isOk())
//            .andExpect(MockMvcResultMatchers.jsonPath("$.mostSpeeches", Matchers.`is`("A")))
//            .andExpect(MockMvcResultMatchers.jsonPath("$.mostSecurity", Matchers.`is`("B")))
//            .andExpect(MockMvcResultMatchers.jsonPath("$.leastWordy", Matchers.`is`("C")))
//    }

    @Test
    fun evaluate_withNotAvailableParam_failed() = runTest {
        mockMvc.perform(get("/evaluate").queryParam("abc", url))
            .andExpect(status().isOk())
            .andExpect { result: MvcResult ->
                val asyncResult = result.asyncResult
                assertTrue(asyncResult is EvaluationServiceException)
                if(asyncResult is EvaluationServiceException){
                    assertEquals(ErrorCode.URL_PARAM_REQUIRED_ERROR.value, asyncResult.message)
                }
            }
    }

    @Test
    fun evaluate_withNotValidUrl_failed() = runTest {
        mockMvc.perform(get("/evaluate").queryParam("url1", "abc"))
            .andExpect(status().isOk())
            .andExpect { result: MvcResult ->
                val asyncResult = result.asyncResult
                assertTrue(asyncResult is EvaluationServiceException)
                if(asyncResult is EvaluationServiceException){
                    assertEquals(ErrorCode.URL_VALIDATION_ERROR.value, asyncResult.message)
                }
            }
    }

    @Test
    @Throws(Exception::class)
    fun evaluate_withUnsupportedProtocol_failed() = runTest {
        mockMvc.perform(get("/evaluate").queryParam("url1", "file:///downloads/file.csv"))
            .andExpect(status().isOk())
            .andExpect { result: MvcResult ->
                val asyncResult = result.asyncResult
                assertTrue(asyncResult is EvaluationServiceException)
                if(asyncResult is EvaluationServiceException){
                    assertEquals(ErrorCode.UNSUPPORTED_PROTOCOL.value, asyncResult.message)
                }
            }
    }

    @Test
    @Throws(Exception::class)
    fun evaluate_withEmptyUrl_failed() = runTest {
        mockMvc.perform(get("/evaluate"))
            .andExpect(status().isOk())
            .andExpect { result: MvcResult ->
                val asyncResult = result.asyncResult
                assertTrue(asyncResult is EvaluationServiceException)
                if(asyncResult is EvaluationServiceException){
                    assertEquals(ErrorCode.URL_PARAM_REQUIRED_ERROR.value, asyncResult.message)
                }
            }
    }
}