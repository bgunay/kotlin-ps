package com.fashiondigital.politicalspeeches

import com.fashiondigital.politicalspeeches.model.Speech
import com.fashiondigital.politicalspeeches.service.impl.CsvParserService
import org.apache.commons.io.IOUtils
import java.nio.charset.StandardCharsets
import java.time.LocalDate


object TestUtils {

    const val SPEAKER_1 = "Alexander Abel"
    const val SPEAKER_2 = "Bernhard Belling"
    const val SPEAKER_3 = "Caesare Collins"
    const val CSV_URL_1 = "https://csv1.com?url=example.csv"
    const val  CSV_URL_2 = "https://csv2.com/url2=example.csv"
    const val  VALID_SPEECHES_1 = "data/valid-speeches-1.csv"
    const val  VALID_SPEECHES_2 = "data/valid-speeches-2.csv"
    const val  INVALID_SPEECHES_DELIMITER = "data/invalid-speeches-delimiter.csv"
    const val  INVALID_COLUMN = "data/invalid-column.csv"
    const val  INVALID_SPEECHES_EMPTY = "data/invalid-speeches-empty.csv"
    const val  INVALID_SPEECHES_DATE = "data/invalid-speeches-date.csv"
    const val  INVALID_SPEECHES_MINUS_WORDS = "data/invalid-speeches-minus.csv"
    const val  INVALID_SPEECHES_MISSING_TOPIC = "data/invalid-speeches-missing_topic.csv"


    fun zerFieldSpeechs(): List<Speech> {
        return listOf()
    }

    fun getResourceContent(path: String): String {
        return IOUtils.toString(TestUtils::class.java.getClassLoader().getResourceAsStream(path),
            StandardCharsets.UTF_8.name())
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
