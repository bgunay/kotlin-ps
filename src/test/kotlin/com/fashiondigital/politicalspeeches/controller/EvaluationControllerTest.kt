package com.fashiondigital.politicalspeeches.controller

import com.fashiondigital.politicalspeeches.model.ErrorCode
import com.fashiondigital.politicalspeeches.model.EvaluationResult
import com.fashiondigital.politicalspeeches.service.ICsvHttpService
import com.fashiondigital.politicalspeeches.service.ICsvParserService
import com.fashiondigital.politicalspeeches.service.IEvaluationService
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyList
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

// TODO: Fix GlobalExceptionHandler problem for suspending controller endpoints


@ExtendWith(SpringExtension::class)
@WebMvcTest
internal class EvaluationControllerTest(@Autowired val mockMvc: MockMvc) {


    @Value("\${csv.server.address}")
    private lateinit var url: String

    @TestConfiguration
    class ControllerTestConfig {
        @Bean
        fun evaluationService() = mockk<IEvaluationService>()
        @Bean
        fun csvParserService() = mockk<ICsvParserService>()
        @Bean
        fun csvHttpService() = mockk<ICsvHttpService>()
    }


    private val evaluationService = mockk<IEvaluationService>(relaxed = true)

    companion object {
        private val EVALUATION_RESULT: EvaluationResult = EvaluationResult(
            mostSpeeches = "A",
            mostSecurity = "B",
            leastWordy = "C"
        )
    }


//    @Test
//    fun evaluate_success() = runTest {
//        coEvery { evaluationService.analyzeSpeeches(anyList()) } returns EVALUATION_RESULT
//        mockMvc.perform(MockMvcRequestBuilders.get("/evaluate").queryParam("url1", url))
//            .andExpect(MockMvcResultMatchers.status().isAccepted())
//            .andExpect(MockMvcResultMatchers.jsonPath("$.mostSpeeches", Matchers.`is`("A")))
//            .andExpect(MockMvcResultMatchers.jsonPath("$.mostSecurity", Matchers.`is`("B")))
//            .andExpect(MockMvcResultMatchers.jsonPath("$.leastWordy", Matchers.`is`("C")))
//    }

    @Test
    fun evaluate_withNotAvailableParam_failed() = runTest {
        coEvery { evaluationService.analyzeSpeeches(anyList()) } returns EVALUATION_RESULT
        mockMvc.perform(get("/evaluate").queryParam("abc", url))
            .andExpect(status().isBadRequest())
            .andExpect { result: MvcResult ->
                assertEquals(
                    ErrorCode.URL_PARAM_REQUIRED_ERROR.value,
                    result.resolvedException!!.message
                )
            }
    }

    @Test
    fun evaluate_withNotValidUrl_failed() = runTest {
        coEvery { evaluationService.analyzeSpeeches(anyList()) } returns EVALUATION_RESULT
        mockMvc.perform(get("/evaluate").queryParam("url1", "abc"))
            .andExpect(status().isBadRequest())
            .andExpect { result: MvcResult ->
                assertEquals(
                    ErrorCode.URL_VALIDATION_ERROR.value,
                    result.resolvedException!!.message
                )
            }
    }

    @Test
    @Throws(Exception::class)
    fun evaluate_withUnsupportedProtocol_failed() = runTest {
        mockMvc.perform(get("/evaluate").queryParam("url1", "file:///downloads/file.csv"))
            .andExpect(status().isBadRequest())
            .andExpect { result: MvcResult ->
                assertEquals(
                    ErrorCode.UNSUPPORTED_PROTOCOL.value,
                    result.resolvedException!!.message
                )
            }
    }

    @Test
    @Throws(Exception::class)
    fun evaluate_withEmptyUrl_failed() = runTest {
        mockMvc.perform(get("/evaluate"))
            .andExpect(status().isBadRequest())
            .andExpect { result: MvcResult ->
                assertEquals(
                    ErrorCode.URL_PARAM_REQUIRED_ERROR.value,
                    result.resolvedException!!.message
                )
            }
    }
}