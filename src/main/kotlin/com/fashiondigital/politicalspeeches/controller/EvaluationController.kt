package com.fashiondigital.politicalspeeches.controller

import com.fashiondigital.politicalspeeches.model.EvaluationResult
import com.fashiondigital.politicalspeeches.service.IEvaluationService
import com.fashiondigital.politicalspeeches.validation.UrlHeaderValidation
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@Tag(name = "political-speech", description = "political-speech")
class EvaluationController(@Autowired val evaluationService: IEvaluationService) {


    @GetMapping("evaluate")
    @Operation(summary="Evaluates Politics in CSV file that you specify",
        description = "Finds most speeches, security and worldly politician.")
    @ApiResponses(value = [
        ApiResponse(responseCode="200", description = "Successful Operation",
            content = [Content(mediaType = "application/json", schema = Schema(implementation = ResponseEntity::class))])
    ])
    suspend fun evaluate(@RequestParam headers: Map<String, String>): ResponseEntity<EvaluationResult> {
        //Only valid if params are like "url1=address,url2=address,urlN..."
        val urlParams: Set<String> = UrlHeaderValidation.extractAndValidateUrlsFromRequest(headers)
        val result: EvaluationResult = evaluationService.evaluate(urlParams.toList())
        return ResponseEntity.ok(result)
    }
}
