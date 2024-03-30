package com.fashiondigital.politicalspeeches.service.impl

import com.fashiondigital.politicalspeeches.model.EvaluationResult
import com.fashiondigital.politicalspeeches.model.SpeakerStats
import com.fashiondigital.politicalspeeches.service.ICsvParserService
import com.fashiondigital.politicalspeeches.service.IEvaluationService
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
        var count = 0
        var max = 0
        for (spkr in speakerStatsMap.keys) {
            if (speakerStatsMap[spkr]?.targetYearSpeeches!! > max) {
                max = speakerStatsMap[spkr]?.targetYearSpeeches!!
                speaker = spkr
                count = 1
            } else if (speakerStatsMap[spkr]?.targetYearSpeeches!! == max) {
                count += 1 //multiple speakers with same speech-count
            }
        }
        //return a unique speaker Otherwise it is null.
        return if (count == 1) speaker else null
    }

    private fun findSpeakerByMostSecuritySpeeches(speakerStatsMap: Map<String, SpeakerStats>): String? {
        var speaker: String? = null
        var count = 0
        var max = 0
        for (spkr in speakerStatsMap.keys) {
            if (speakerStatsMap[spkr]?.securitySpeeches!! > max) {
                max = speakerStatsMap[spkr]?.securitySpeeches!!
                speaker = spkr
                count = 1
            } else if (speakerStatsMap[spkr]?.securitySpeeches!! == max) {
                count += 1 //multiple speakers with same speech-count
            }
        }
        //return a unique speaker Otherwise it is null.
        return if (count == 1) speaker else null
    }

    private fun findSpeakerByLeastWordySpeeches(speakerStatsMap: Map<String, SpeakerStats>): String? {
        var speaker: String? = null
        var count = 0
        var min = Int.MAX_VALUE
        for (spkr in speakerStatsMap.keys) {
            if (speakerStatsMap[spkr]?.overallWords!! < min) {
                min = speakerStatsMap[spkr]?.overallWords!!
                speaker = spkr
                count = 1
            } else if (speakerStatsMap[spkr]?.overallWords!! == min) {
                count += 1 //multiple speakers with same word-count
            }
        }
        //return a unique speaker Otherwise it is null.
        return if (count == 1) speaker else null
    }
}
