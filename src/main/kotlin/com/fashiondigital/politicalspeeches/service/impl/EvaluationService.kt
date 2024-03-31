package com.fashiondigital.politicalspeeches.service.impl

import com.fashiondigital.politicalspeeches.model.EvaluationResult
import com.fashiondigital.politicalspeeches.model.Speech
import com.fashiondigital.politicalspeeches.service.IEvaluationService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service


@Service
class EvaluationService : IEvaluationService {


    private val log: Logger = LoggerFactory.getLogger(EvaluationService::class.java)

    @Value("\${speech.target-year}")
    private val targetYear = 0

    @Value("\${speech.security-topic}")
    private val securityTopic: String? = null

    override fun analyzeSpeeches(speeches: List<Speech>): EvaluationResult {
        log.info("Analyzing Speeches")
        val mostSpeeches = findUniqueMax(speeches.filter { it.date.year == targetYear }, Speech::speaker)
        val mostSecurity = findUniqueMax(speeches.filter { it.topic == securityTopic }, Speech::speaker)
        val leastWordy = findUniqueMin(speeches, Speech::speaker)

        return EvaluationResult(
            mostSpeeches = mostSpeeches,
            mostSecurity = mostSecurity,
            leastWordy = leastWordy
        )
    }

    fun findUniqueMax(speeches: List<Speech>, selector: (Speech) -> String): String? {
        val counts = speeches.groupingBy(selector).eachCount()
        val maxCount = counts.maxByOrNull { it.value }?.value
        return counts.filterValues { it == maxCount }.keys.singleOrNull()
    }

    private fun findUniqueMin(speeches: List<Speech>, selector: (Speech) -> String): String? {
        val totalWords = speeches.groupingBy(selector).fold(0) { acc, speech -> acc + speech.wordCount }
        val minWords = totalWords.minByOrNull { it.value }?.value
        return totalWords.filterValues { it == minWords }.keys.singleOrNull()
    }
}
