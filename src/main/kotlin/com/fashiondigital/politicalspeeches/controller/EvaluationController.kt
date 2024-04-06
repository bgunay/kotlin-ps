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


    @GetMapping("evaluate")
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
    suspend fun evaluate(@RequestParam headers: Map<String, String>): ResponseEntity<EvaluationResult> =
        coroutineScope {
            //Only valid if params are like "url1=address,url2=address,urlN..."
            val urlParams: Set<String> = ValidationUtil.extractAndValidateUrlsFromRequest(headers)
            val csvData = csvHttpService.parseUrlsAndFetchCsvData(urlParams)
            val speeches = csvParserService.parseCSV(csvData)
            val result: EvaluationResult = evaluationService.analyzeSpeeches(speeches)
            ResponseEntity.accepted().body(result)
        }
}
