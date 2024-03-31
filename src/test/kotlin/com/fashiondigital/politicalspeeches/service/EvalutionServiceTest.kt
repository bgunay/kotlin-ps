package com.fashiondigital.politicalspeeches.service


import com.fashiondigital.politicalspeeches.model.EvaluationResult
import com.fashiondigital.politicalspeeches.model.Speech
import com.fashiondigital.politicalspeeches.service.impl.CsvParserService
import com.fashiondigital.politicalspeeches.service.impl.EvaluationService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.util.ReflectionTestUtils
import java.time.LocalDate


@ExtendWith(MockitoExtension::class)
internal class EvaluationServiceTest {
    @InjectMocks
    private lateinit var evaluationService: EvaluationService

    @Mock
    private lateinit var csvParserService: ICsvParserService

    companion object {
        private const val SPEAKER_1 = "Alexander Abel"
        private const val SPEAKER_2 = "Bernhard Belling"
        private const val SPEAKER_3 = "Caesare Collins"
        private val URLS = setOf("Url-1")
    }
    val validSpeeches1 = mutableListOf(
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

    val notUniqueSpeechs = mutableListOf(
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
    @Test
    fun evaluate_withAllValidFields() {

        ReflectionTestUtils.setField(evaluationService, "targetYear", 2013);
        ReflectionTestUtils.setField(evaluationService, "securityTopic", "homeland security");

        val result: EvaluationResult = evaluationService.analyzeSpeeches(validSpeeches1)

        assertNull(result.mostSpeeches)
        assertThat(result.mostSecurity).isEqualTo(SPEAKER_1)
        assertThat(result.leastWordy).isEqualTo(SPEAKER_3)
    }

    @Test
    fun evaluate_withNotUniqueFields() {
        ReflectionTestUtils.setField(evaluationService, "targetYear", 2013);
        ReflectionTestUtils.setField(evaluationService, "securityTopic", "homeland security");

        val result: EvaluationResult = evaluationService.analyzeSpeeches(notUniqueSpeechs)

        assertThat(result.mostSpeeches).isNull()
        assertThat(result.mostSecurity).isEqualTo(SPEAKER_1)
        assertThat(result.leastWordy).isNull()
    }

    @Test
    fun evaluate_withZeroFields() {
        ReflectionTestUtils.setField(evaluationService, "targetYear", 2013);
        ReflectionTestUtils.setField(evaluationService, "securityTopic", "homeland security");

        validSpeeches1.forEach {
            it.wordCount = 0
        }

        val result: EvaluationResult = evaluationService.analyzeSpeeches(validSpeeches1)

        assertThat(result.mostSpeeches).isNull()
        assertThat(result.mostSecurity).isEqualTo(SPEAKER_1)
        assertThat(result.leastWordy).isNull()
    }
}