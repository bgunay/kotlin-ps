package com.fashiondigital.politicalspeeches.service


import com.fashiondigital.politicalspeeches.model.EvaluationResult
import com.fashiondigital.politicalspeeches.service.impl.EvaluationService
import com.fashiondigital.politicalspeeches.utils.SpeechUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.util.ReflectionTestUtils


@ExtendWith(MockitoExtension::class)
internal class EvaluationServiceTest {
    @InjectMocks
    private lateinit var evaluationService: EvaluationService

    @Mock
    private lateinit var csvParserService: ICsvParserService


    @Test
    fun evaluate_withAllValidFields() {

        ReflectionTestUtils.setField(evaluationService, "targetYear", 2013);
        ReflectionTestUtils.setField(evaluationService, "securityTopic", "homeland security");

        val result: EvaluationResult = evaluationService.analyzeSpeeches(SpeechUtils.validSpeeches1)

        assertNull(result.mostSpeeches)
        assertThat(result.mostSecurity).isEqualTo(SpeechUtils.SPEAKER_1)
        assertThat(result.leastWordy).isEqualTo(SpeechUtils.SPEAKER_3)
    }

    @Test
    fun evaluate_withNotUniqueFields() {
        ReflectionTestUtils.setField(evaluationService, "targetYear", 2013);
        ReflectionTestUtils.setField(evaluationService, "securityTopic", "homeland security");

        val result: EvaluationResult = evaluationService.analyzeSpeeches(SpeechUtils.notUniqueSpeechs)

        assertThat(result.mostSpeeches).isNull()
        assertThat(result.mostSecurity).isEqualTo(SpeechUtils.SPEAKER_1)
        assertThat(result.leastWordy).isNull()
    }

    @Test
    fun evaluate_withZeroFields() {
        ReflectionTestUtils.setField(evaluationService, "targetYear", 2013);
        ReflectionTestUtils.setField(evaluationService, "securityTopic", "homeland security");

        val result: EvaluationResult = evaluationService.analyzeSpeeches(SpeechUtils.zerFieldSpeechs())

        assertThat(result.mostSpeeches).isNull()
        assertThat(result.mostSecurity).isEqualTo(SpeechUtils.SPEAKER_1)
        assertThat(result.leastWordy).isNull()
    }
}