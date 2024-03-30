package com.fashiondigital.politicalspeeches.service.impl

import com.fashiondigital.politicalspeeches.model.EvaluationResult

interface IEvaluationService {
    fun evaluate(urls: Set<String>): EvaluationResult
}