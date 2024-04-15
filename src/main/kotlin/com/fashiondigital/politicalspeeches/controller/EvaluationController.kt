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
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Evaluates Politics in CSV file that you specify. Finds most speeches, security and worldly politician.
 */
@RestController
@Tag(name = "political-speech", description = "political-speech")
class EvaluationController(
    @Autowired val evaluationService: IEvaluationService,
    @Autowired val csvParserService: ICsvParserService,
    @Autowired val csvHttpService: ICsvHttpService,
) {

    /**
     * Evaluates Politics in CSV file that you specify. Finds most speeches, security and worldly politician.
     *
     * @param headers The headers containing the URLs of the CSV files to be evaluated.
     * @return The evaluation result of the specified CSV files.
     *
     * coroutineScope ensures that the coroutine it contains
     * will not be cancelled until all its child coroutines have completed.
     * The coroutineScope ensures that the entire process is completed
     * before the function returns, and it prevents the coroutine from being cancelled prematurely
     */
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
    suspend fun evaluate(@RequestParam headers: Map<String, String>) = coroutineScope {
        val urlParams: Set<String> = ValidationUtil.extractAndValidateUrlsFromRequest(headers)
        val csvData = csvHttpService.fetchCsvData(urlParams)
        val speeches = csvParserService.parseCSV(csvData)
        val result: EvaluationResult = evaluationService.analyzeSpeeches(speeches)
        result
    }

    /**
     * Evaluates Politics in CSV file that you specify. Finds most speeches, security and worldly politician.
     *
     * @param headers The headers containing the URLs of the CSV files to be evaluated.
     * @return A Flow of the evaluation result of the specified CSV files.
     *  Flow returns List, its flux equivalent in coroutines world.
     *  fun handler(): Flux<T> becomes fun handler(): Flow<T>
     */
    @GetMapping("evaluate2")
    suspend fun evaluate2(@RequestParam headers: Map<String, String>): Flow<EvaluationResult>? {
        val urlParams: Set<String> = ValidationUtil.extractAndValidateUrlsFromRequest(headers)
        val csvData = csvHttpService.fetchCsvData(urlParams)
        val speeches = csvParserService.parseCSV(csvData)
        val result: EvaluationResult = evaluationService.analyzeSpeeches(speeches)
        return flowOf(result)
    }


    /**
     * Evaluates Politics in CSV file that you specify. Finds most speeches, security and worldly politician.
     *
     * @param headers The headers containing the URLs of the CSV files to be evaluated.
     * @return The evaluation result of the specified CSV files wrapped in a ResponseEntity.
     *
     * fun handler(): Mono<Void> becomes suspend fun handler()
     *
     */
    @GetMapping("evaluate3")
    suspend fun evaluate3(@RequestParam headers: Map<String, String>): ResponseEntity<EvaluationResult> {
            val urlParams: Set<String> = ValidationUtil.extractAndValidateUrlsFromRequest(headers)
            val csvData = csvHttpService.fetchCsvData(urlParams)
            val speeches = csvParserService.parseCSV(csvData)
            val result: EvaluationResult = evaluationService.analyzeSpeeches(speeches)
            return ResponseEntity.ok(result)
        }
}