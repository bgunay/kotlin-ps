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
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.request
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
            .andExpect { it.request.isAsyncStarted }
            .andExpect { it.asyncResult is Collection<*> }
            .andExpect {
                val evaluationResult = (it.asyncResult as Collection<*>).first() as EvaluationResult
                assertEquals(EVALUATION_RESULT, evaluationResult)
            }
    }


    @Test
    fun evaluate_withNotAvailableParam_failed() = runTest {
        val mvcResult = mockMvc.perform(get("/evaluate").queryParam("abc", url))
            .andExpect(request().asyncStarted())
            .andExpect { it.asyncResult is EvaluationServiceException }
            .andExpect { (it.asyncResult as EvaluationServiceException).message.equals(ErrorCode.URL_PARAM_REQUIRED_ERROR.value) }
            .andReturn()

        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect { status().isBadRequest }
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
    }

    @Test
    fun evaluate_withNotValidUrl_failed() = runTest {
        val mvcResult = mockMvc.perform(get("/evaluate").queryParam("url1", "abc"))
            .andExpect(request().asyncStarted())
            .andExpect { it.asyncResult is EvaluationServiceException }
            .andExpect { (it.asyncResult as EvaluationServiceException).message.equals(ErrorCode.URL_VALIDATION_ERROR.value) }
            .andReturn()

        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect { status().isBadRequest }
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
    }

    @Test
    fun evaluate_withUnsupportedProtocol_failed() = runTest {
        val mvcResult = mockMvc.perform(get("/evaluate").queryParam("url1", "file:///downloads/file.csv"))
            .andExpect(request().asyncStarted())
            .andExpect { it.asyncResult is EvaluationServiceException }
            .andExpect { (it.asyncResult as EvaluationServiceException).message.equals(ErrorCode.UNSUPPORTED_PROTOCOL.value) }
            .andReturn()

        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect { status().isBadRequest }
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
    }

    @Test
    fun evaluate_withEmptyUrl_failed() = runTest {
        val mvcResult = mockMvc.perform(get("/evaluate"))
            .andExpect(request().asyncStarted())
            .andExpect { it.asyncResult is EvaluationServiceException }
            .andExpect { (it.asyncResult as EvaluationServiceException).message.equals(ErrorCode.URL_PARAM_REQUIRED_ERROR.value) }
            .andReturn()

        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect { status().isBadRequest }
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
    }
}