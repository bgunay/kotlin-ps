package com.fashiondigital.politicalspeeches.service

import com.fashiondigital.politicalspeeches.model.EvaluationResult
import com.fashiondigital.politicalspeeches.model.SpeakerStats
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension


@ExtendWith(MockitoExtension::class)
internal class EvaluationServiceTest {
    @InjectMocks
    private lateinit var evaluationService: EvaluationService

    @Mock
    private lateinit var csvParserService: CsvParserService

    companion object {
        private const val SPEAKER_1 = "Speaker 1"
        private const val SPEAKER_2 = "Speaker 2"
        private const val SPEAKER_3 = "Speaker 3"
        private val URLS = setOf("Url-1")
    }

    @Test
    fun evaluate_withAllValidFields() {
        val speakerStatsMap: Map<String, SpeakerStats> = java.util.Map.of<String, SpeakerStats>(
                SPEAKER_1, SpeakerStats(1, 2, 1),
                SPEAKER_2, SpeakerStats(2, 3, 2),
                SPEAKER_3, SpeakerStats(3, 1, 3)
        )
        Mockito.`when`(csvParserService.parseCSVsByUrls(URLS)).thenReturn(speakerStatsMap)

        val result: EvaluationResult = evaluationService.evaluate(URLS)

        assertThat(result.mostSpeeches).isEqualTo(SPEAKER_3)
        assertThat(result.mostSecurity).isEqualTo(SPEAKER_2)
        assertThat(result.leastWordy).isEqualTo(SPEAKER_1)
    }

    @Test
    fun evaluate_withNotUniqueFields() {
        val speakerStatsMap: Map<String, SpeakerStats> = java.util.Map.of<String, SpeakerStats>(
                SPEAKER_1, SpeakerStats(1, 2, 3),
                SPEAKER_2, SpeakerStats(1, 2, 3),
                SPEAKER_3, SpeakerStats(1, 2, 3)
        )
        Mockito.`when`(csvParserService.parseCSVsByUrls(URLS)).thenReturn(speakerStatsMap)

        val result: EvaluationResult = evaluationService.evaluate(URLS)

        assertThat(result.mostSpeeches).isNull()
        assertThat(result.mostSecurity).isNull()
        assertThat(result.leastWordy).isNull()
    }

    @Test
    fun evaluate_withZeroFields() {
        val speakerStatsMap: Map<String, SpeakerStats> = java.util.Map.of<String, SpeakerStats>(
                SPEAKER_1, SpeakerStats(0, 0, 0),
                SPEAKER_2, SpeakerStats(0, 0, 0),
                SPEAKER_3, SpeakerStats(0, 0, 0)
        )
        Mockito.`when`(csvParserService.parseCSVsByUrls(URLS)).thenReturn(speakerStatsMap)

        val result: EvaluationResult = evaluationService.evaluate(URLS)

        assertThat(result.mostSpeeches).isNull()
        assertThat(result.mostSecurity).isNull()
        assertThat(result.leastWordy).isNull()
    }
}