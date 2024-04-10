package com.fashiondigital.politicalspeeches.controller

import com.fashiondigital.politicalspeeches.model.EvaluationResult
import com.fashiondigital.politicalspeeches.service.ICsvHttpService
import com.fashiondigital.politicalspeeches.service.ICsvParserService
import com.fashiondigital.politicalspeeches.service.IEvaluationService
import com.fashiondigital.politicalspeeches.validation.ValidationUtil
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@Tag(name = "political-speech", description = "political-speech")
class EvaluationController(
    @Autowired val evaluationService: IEvaluationService,
    @Autowired val csvParserService: ICsvParserService,
    @Autowired val csvHttpService: ICsvHttpService,
) {


    @Operation(
        summary = "Evaluates Politics in CSV file that you specify",
        description = "Finds most speeches, security and worldly politician."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Successful Operation",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = ResponseEntity::class)
                )]
            )
        ]
    )
    @GetMapping("evaluate")
    fun evaluate(@RequestParam headers: Map<String, String>): ResponseEntity<EvaluationResult> =
        runBlocking {
            val urlParams: Set<String> = ValidationUtil.extractAndValidateUrlsFromRequest(headers)
            val csvData = csvHttpService.parseUrlsAndFetchCsvData(urlParams)
            val speeches = csvParserService.parseCSV(csvData)
            val result: EvaluationResult = evaluationService.analyzeSpeeches(speeches)
            return@runBlocking ResponseEntity.accepted().body(result)
        }

    @GetMapping("evaluate2")
    suspend fun evaluate2(@RequestParam headers: Map<String, String>): Flow<Any>? {
        try {
            val urlParams: Set<String> = ValidationUtil.extractAndValidateUrlsFromRequest(headers)
            val csvData = csvHttpService.parseUrlsAndFetchCsvData(urlParams)
            val speeches = csvParserService.parseCSV(csvData)
            val result: EvaluationResult = evaluationService.analyzeSpeeches(speeches)
            return flowOf(result)
        } catch (ex: Exception) {
            return flowOf(ex)
        }
    }


    @OptIn(DelicateCoroutinesApi::class)
    @GetMapping("evaluate3")
    suspend fun evaluate3(@RequestParam headers: Map<String, String>) = GlobalScope.async {
        val urlParams: Set<String> = ValidationUtil.extractAndValidateUrlsFromRequest(headers)
        val csvData = csvHttpService.parseUrlsAndFetchCsvData(urlParams)
        val speeches = csvParserService.parseCSV(csvData)
        val result: EvaluationResult = evaluationService.analyzeSpeeches(speeches)
        result
    }
}
