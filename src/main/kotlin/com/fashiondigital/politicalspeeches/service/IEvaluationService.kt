package com.fashiondigital.politicalspeeches.service

import com.fashiondigital.politicalspeeches.model.EvaluationResult

interface IEvaluationService {
    fun evaluate(urls: Set<String>): EvaluationResult
}