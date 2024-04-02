package com.fashiondigital.politicalspeeches.integration

import com.fashiondigital.politicalspeeches.exception.EvaluationServiceException
import com.fashiondigital.politicalspeeches.model.ErrorCode
import com.fashiondigital.politicalspeeches.service.impl.CsvParserService
import com.fashiondigital.politicalspeeches.service.impl.EvaluationService
import com.fashiondigital.politicalspeeches.util.HttpClient
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.client.RestTemplate


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HttpClientIntegrationTest {


    @Autowired
    private lateinit var csvParserService: CsvParserService

    @Autowired
    private val restTemplate= RestTemplate()

    @Autowired
    private val httpClient = HttpClient(restTemplate)

    @Autowired
    private val evaluationService = EvaluationService()

    @Value("\${csv.server.address}")
    private val serverAddress: String? = null


    @Test
    fun `test successful CSV download and processing`() = runBlocking {
        val urls = listOf(
            "${serverAddress}/valid-speeches-1.csv",
            "${serverAddress}/valid-speeches-2.csv"
        )

        val allSpeeches = urls.flatMap { url ->
            val csvContent = httpClient.getHttpCSVResponse(url).body
            val parseCSV = csvParserService.parseCSV(mutableListOf(csvContent))
            parseCSV
        }

        val statistics = evaluationService.analyzeSpeeches(allSpeeches)

        assertEquals("Alexander Abel", statistics.mostSpeeches)
        assertEquals("Alexander Abel", statistics.mostSecurity)
        assertEquals("Caesare Collins", statistics.leastWordy)
    }

    @Test
    fun `test CSV download failure`() = runBlocking {
        val invalidUrl = "${serverAddress}/invalid-url"

        val exception = assertThrows<EvaluationServiceException> {
            httpClient.getHttpCSVResponse(invalidUrl)
        }

        assertTrue(exception.message?.contains(ErrorCode.URL_READER_ERROR.value) == true)
        assertTrue(exception.message?.contains("Failed to read url") == true)
    }
}
