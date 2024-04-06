package com.fashiondigital.politicalspeeches.integration

import com.fashiondigital.politicalspeeches.exception.EvaluationServiceException
import com.fashiondigital.politicalspeeches.model.ErrorCode
import com.fashiondigital.politicalspeeches.service.impl.CsvParserService
import com.fashiondigital.politicalspeeches.service.impl.EvaluationService
import com.fashiondigital.politicalspeeches.util.HttpClient
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import kotlinx.coroutines.test.runTest
import org.springframework.web.reactive.function.client.WebClient


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HttpClientIntegrationTest {


    @Autowired
    private lateinit var csvParserService: CsvParserService

    @Autowired
    private lateinit var webClient: WebClient

    @Autowired
    private val httpClient = HttpClient(webClient)

    @Autowired
    private val evaluationService = EvaluationService()

    @Value("\${csv.server.address}")
    private val serverAddress: String? = null


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test successful CSV download and processing`() = runTest {
        val urls = listOf(
            "${serverAddress}/valid-speeches-1.csv",
            "${serverAddress}/valid-speeches-2.csv"
        )

        val allSpeeches = urls.flatMap { url ->
            val csvContent = httpClient.getHttpCSVResponse(url)!!.body
            val parseCSV = csvParserService.parseCSV(mutableListOf(csvContent))
            parseCSV
        }

        val statistics = evaluationService.analyzeSpeeches(allSpeeches)

        assertEquals("Alexander Abel", statistics.mostSpeeches)
        assertEquals("Alexander Abel", statistics.mostSecurity)
        assertEquals("Caesare Collins", statistics.leastWordy)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test CSV download failure`() = runTest {
        val invalidUrl = "${serverAddress}/invalid-url"

        val exception = assertThrows<EvaluationServiceException> {
            httpClient.getHttpCSVResponse(invalidUrl)
        }

        assertTrue(exception.message?.contains(ErrorCode.URL_READER_ERROR.value) == true)
        assertTrue(exception.message?.contains("Failed to read url") == true)
    }
}
