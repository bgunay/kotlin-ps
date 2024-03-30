package com.fashiondigital.politicalspeeches.controller

import com.fashiondigital.politicalspeeches.validation.UrlHeaderValidation
import com.fashiondigital.politicalspeeches.model.EvaluationResult
import com.fashiondigital.politicalspeeches.service.IEvaluationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
class EvaluationController(@Autowired val evaluationService: IEvaluationService) {
    @GetMapping("evaluate")
    fun evaluate(@RequestParam headers: Map<String, String>): ResponseEntity<EvaluationResult> {
        //Only valid if params are like url1, url2, urlN...
        val urlParams: Set<String> = UrlHeaderValidation.extractAndValidateUrlsFromRequest(headers)
        val result: EvaluationResult = evaluationService.evaluate(urlParams)
        return ResponseEntity.ok(result)
    }
}
