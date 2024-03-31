package com.fashiondigital.politicalspeeches.utils

import com.fashiondigital.politicalspeeches.model.Speech
import com.fashiondigital.politicalspeeches.service.impl.CsvParserService
import java.time.LocalDate

object SpeechUtils {


      const val SPEAKER_1 = "Alexander Abel"
      const val SPEAKER_2 = "Bernhard Belling"
      const val SPEAKER_3 = "Caesare Collins"

    
    fun zerFieldSpeechs(): List<Speech> {
        val zeroList = validSpeeches1.map {
            Speech(it.speaker, it.topic, it.date, 0)
        }.toList()

        return zeroList;
    }
        

    val validSpeeches1 = listOf(
        Speech(
            SPEAKER_1,
            "education policy",
            LocalDate.parse("2012-10-30", CsvParserService.DATE_TIME_FORMATTER),
            5310
        ),
        Speech(
            SPEAKER_2,
            "coal subsidies",
            LocalDate.parse("2012-11-05", CsvParserService.DATE_TIME_FORMATTER),
            1210
        ),
        Speech(
            SPEAKER_3,
            "coal subsidies",
            LocalDate.parse("2012-11-06", CsvParserService.DATE_TIME_FORMATTER),
            1119
        ),
        Speech(
            SPEAKER_1,
            "homeland security",
            LocalDate.parse("2012-12-11", CsvParserService.DATE_TIME_FORMATTER),
            100
        )
    )

    val notUniqueSpeechs = listOf(
        Speech(
            SPEAKER_1,
            "education policy",
            LocalDate.parse("2012-10-30", CsvParserService.DATE_TIME_FORMATTER),
            1210
        ),
        Speech(
            SPEAKER_2,
            "coal subsidies",
            LocalDate.parse("2012-11-05", CsvParserService.DATE_TIME_FORMATTER),
            1210
        ),
        Speech(
            SPEAKER_3,
            "coal subsidies",
            LocalDate.parse("2012-11-06", CsvParserService.DATE_TIME_FORMATTER),
            1210
        ),
        Speech(
            SPEAKER_1,
            "homeland security",
            LocalDate.parse("2012-12-11", CsvParserService.DATE_TIME_FORMATTER),
            1210
        )
    )
}