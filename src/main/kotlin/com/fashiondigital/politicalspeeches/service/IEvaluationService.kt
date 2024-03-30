package com.fashiondigital.politicalspeeches.service

import com.fashiondigital.politicalspeeches.model.EvaluationResult
import com.fashiondigital.politicalspeeches.model.Speech

interface IEvaluationService {
    fun analyzeSpeeches(urls: List<Speech>): EvaluationResult
}