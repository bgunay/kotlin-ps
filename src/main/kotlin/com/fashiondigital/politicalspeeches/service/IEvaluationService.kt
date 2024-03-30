package com.fashiondigital.politicalspeeches.service

import com.fashiondigital.politicalspeeches.model.EvaluationResult

interface IEvaluationService {
      suspend fun evaluate(urls: List<String>): EvaluationResult
}