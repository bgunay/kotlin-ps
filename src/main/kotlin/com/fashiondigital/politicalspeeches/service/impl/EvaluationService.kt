package com.fashiondigital.politicalspeeches.service.impl

import com.fashiondigital.politicalspeeches.model.EvaluationResult
import com.fashiondigital.politicalspeeches.model.Speech
import com.fashiondigital.politicalspeeches.service.IEvaluationService
import com.fashiondigital.politicalspeeches.util.LoggerDelegate
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service


@Service
class EvaluationService : IEvaluationService {

    companion object {
        private val log by LoggerDelegate()
    }

    @Value("\${speech.target-year}")
    private val targetYear = 0

    @Value("\${speech.security-topic}")
    private lateinit var securityTopic:String

    override fun analyzeSpeeches(speeches: List<Speech>): EvaluationResult {
        log.info("Analyzing Speeches")
        val selector = Speech::speaker
        val mostSpeeches = findUniqueMax(speeches.filter { it.date.year == targetYear }, selector)
        val mostSecurity = findUniqueMax(speeches.filter { it.topic == securityTopic }, selector)
        val leastWordy = findUniqueMin(speeches, selector)

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
