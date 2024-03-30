package com.fashiondigital.politicalspeeches.service

import com.fashiondigital.politicalspeeches.model.EvaluationResult
import com.fashiondigital.politicalspeeches.model.SpeakerStats
import com.fashiondigital.politicalspeeches.service.impl.ICsvParserService
import com.fashiondigital.politicalspeeches.service.impl.IEvaluationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class EvaluationService(@Autowired val csvParserService: ICsvParserService) : IEvaluationService {
    override fun evaluate(urls: Set<String>): EvaluationResult {
        val speakerStatsMap = csvParserService.parseCSVsByUrls(urls)
        return EvaluationResult(
            mostSpeeches = findSpeakerByMostSpeeches(speakerStatsMap),
            mostSecurity = findSpeakerByMostSecuritySpeeches(speakerStatsMap),
            leastWordy = findSpeakerByLeastWordySpeeches(speakerStatsMap)
        )
    }

    private fun findSpeakerByMostSpeeches(speakerStatsMap: Map<String, SpeakerStats>): String? {
        var speaker: String? = null
        var max = 0
        for ((key, value) in speakerStatsMap) {
            if (value.targetYearSpeeches > max) {
                max = value.targetYearSpeeches
                speaker = key
            }
        }
        return speaker
    }

    private fun findSpeakerByMostSecuritySpeeches(speakerStatsMap: Map<String, SpeakerStats>): String? {
        var speaker: String? = null
        var max = 0
        for ((key, value) in speakerStatsMap) {
            if (value.securitySpeeches > max) {
                max = value.securitySpeeches
                speaker = key
            }
        }
        return speaker
    }

    private fun findSpeakerByLeastWordySpeeches(speakerStatsMap: Map<String, SpeakerStats>): String? {
        var speaker: String? = null
        var min = Int.MAX_VALUE
        for ((key, value) in speakerStatsMap) {
            if (value.overallWords < min) {
                min = value.overallWords
                speaker = key
            }
        }
        return speaker
    }
}
