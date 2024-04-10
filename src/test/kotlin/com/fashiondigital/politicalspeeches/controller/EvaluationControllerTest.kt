package com.fashiondigital.politicalspeeches.controller

import com.fashiondigital.politicalspeeches.exception.EvaluationServiceException
import com.fashiondigital.politicalspeeches.exception.GlobalExceptionHandler
import com.fashiondigital.politicalspeeches.model.ErrorCode
import com.fashiondigital.politicalspeeches.model.EvaluationResult
import com.fashiondigital.politicalspeeches.service.ICsvHttpService
import com.fashiondigital.politicalspeeches.service.ICsvParserService
import com.fashiondigital.politicalspeeches.service.IEvaluationService
import org.hamcrest.Matchers
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyList
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers


@AutoConfigureWebClient
@Import(GlobalExceptionHandler::class)
@WebMvcTest(controllers = [EvaluationController::class])
internal class EvaluationControllerTest(@Autowired private var mockMvc: MockMvc) {

    @MockBean
    private lateinit var evaluationService: IEvaluationService

    @MockBean
    private lateinit var csvParserService: ICsvParserService

    @MockBean
    private lateinit var csvHttpService: ICsvHttpService

    @Value("\${csv.server.address}")
    private val url :String? = null

    companion object {
        private val EVALUATION_RESULT: EvaluationResult = EvaluationResult(
                mostSpeeches = "A",
                mostSecurity = "B",
                leastWordy = "C"
        )
    }


    @Test
    fun evaluate_success() {
        Mockito.`when`(evaluationService.analyzeSpeeches(anyList())).thenReturn(EVALUATION_RESULT)
        mockMvc.perform(MockMvcRequestBuilders.get("/evaluate").queryParam("url1", url))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.mostSpeeches", Matchers.`is`("A")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.mostSecurity", Matchers.`is`("B")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.leastWordy", Matchers.`is`("C")))
    }

    @Test
    @Throws(EvaluationServiceException::class)
    fun evaluate_withNotAvailableParam_failed() {
        Mockito.`when`(evaluationService.analyzeSpeeches(anyList())).thenReturn(EVALUATION_RESULT)
        mockMvc.perform(MockMvcRequestBuilders.get("/evaluate").queryParam("abc", url))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect { result: MvcResult -> assertEquals(ErrorCode.URL_PARAM_REQUIRED_ERROR.value, result.resolvedException!!.message) }
    }

    @Test
    @Throws(EvaluationServiceException::class)
    fun evaluate_withNotValidUrl_failed() {
        Mockito.`when`(evaluationService.analyzeSpeeches(anyList())).thenReturn(EVALUATION_RESULT)
        mockMvc.perform(MockMvcRequestBuilders.get("/evaluate").queryParam("url1", "abc"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect { result: MvcResult -> assertEquals(ErrorCode.URL_VALIDATION_ERROR.value, result.resolvedException!!.message) }
    }

    @Test
    @Throws(EvaluationServiceException::class)
    fun evaluate_withUnsupportedProtocol_failed() {
        mockMvc.perform(MockMvcRequestBuilders.get("/evaluate").queryParam("url1", "file:///downloads/file.csv"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect { result: MvcResult -> assertEquals(ErrorCode.UNSUPPORTED_PROTOCOL.value, result.resolvedException!!.message) }
    }

    @Test
    @Throws(EvaluationServiceException::class)
    fun evaluate_withEmptyUrl_failed() {
        mockMvc.perform(MockMvcRequestBuilders.get("/evaluate"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect { result: MvcResult -> assertEquals(ErrorCode.URL_PARAM_REQUIRED_ERROR.value, result.resolvedException!!.message) }
    }
}