package com.fashiondigital.politicalspeeches.service

import com.fashiondigital.politicalspeeches.model.EvaluationResult
import com.fashiondigital.politicalspeeches.model.Speech

interface IEvaluationService {
    fun analyzeSpeeches(speeches: List<Speech>): EvaluationResult
}