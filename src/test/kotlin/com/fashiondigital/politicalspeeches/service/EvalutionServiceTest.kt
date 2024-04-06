package com.fashiondigital.politicalspeeches.service


import com.fashiondigital.politicalspeeches.TestUtils
import com.fashiondigital.politicalspeeches.model.EvaluationResult
import com.fashiondigital.politicalspeeches.service.impl.EvaluationService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.util.ReflectionTestUtils


@ExtendWith(MockitoExtension::class)
internal class EvaluationServiceTest {
    @InjectMocks
    private lateinit var evaluationService: EvaluationService

    @Test
    fun evaluate_withAllValidFields() {

        ReflectionTestUtils.setField(evaluationService, "targetYear", 2013)
        ReflectionTestUtils.setField(evaluationService, "securityTopic", "homeland security")
        val result: EvaluationResult = evaluationService.analyzeSpeeches(TestUtils.validSpeeches1)

        assertNull(result.mostSpeeches)
        assertThat(result.mostSecurity).isEqualTo(TestUtils.SPEAKER_1)
        assertThat(result.leastWordy).isEqualTo(TestUtils.SPEAKER_3)
    }

    @Test
    fun evaluate_withNotUniqueFields() {
        ReflectionTestUtils.setField(evaluationService, "targetYear", 2013)
        ReflectionTestUtils.setField(evaluationService, "securityTopic", "homeland security")
        val result: EvaluationResult = evaluationService.analyzeSpeeches(TestUtils.notUniqueSpeechs)

        assertThat(result.mostSpeeches).isNull()
        assertThat(result.mostSecurity).isEqualTo(TestUtils.SPEAKER_1)
        assertThat(result.leastWordy).isNull()
    }

    @Test
    fun evaluate_withZeroFields() {
        ReflectionTestUtils.setField(evaluationService, "targetYear", 2013)
        ReflectionTestUtils.setField(evaluationService, "securityTopic", "homeland security")

        val result: EvaluationResult = evaluationService.analyzeSpeeches(TestUtils.zerFieldSpeechs())

        assertThat(result.mostSpeeches).isNull()
        assertThat(result.mostSecurity).isNull()
        assertThat(result.leastWordy).isNull()
    }

}