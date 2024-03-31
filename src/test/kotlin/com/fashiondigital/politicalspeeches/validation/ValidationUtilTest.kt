package com.fashiondigital.politicalspeeches.validation

import com.fashiondigital.politicalspeeches.exception.EvaluationServiceException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ValidationUtilTest{
    @Test
    fun `given a request with valid URL headers, when the function is called, then it should return the URLs as a set`() {
        // given
        val headers = mapOf(
            "url1" to "https://example.com/valid-speeches-1.csv",
            "url2" to "https://example.com/video.mp4",
            "url3" to "https://example.com/other"
        )

        // when
        val result = ValidationUtil.extractAndValidateUrlsFromRequest(headers)

        // then
        assertThat(result).contains(*headers.values.toTypedArray())
    }

    @Test
    fun `given a request with an invalid URL header, when the function is called, then it should throw an exception`() {
        // given
        val headers = mapOf(
            "url1" to "https://example.com/image.jpg",
            "url2" to "ftp://example.com/video.mp4", // invalid URL
            "url3" to "https://example.com/other"
        )

        // when and then
        assertThrows<EvaluationServiceException> {
            ValidationUtil.extractAndValidateUrlsFromRequest(headers)
        }
    }

    @Test
    fun `given a request without any URL headers, when the function is called, then it should throw an exception`() {
        // given
        val headers = emptyMap<String, String>()

        // when and then
        assertThrows<EvaluationServiceException> {
            ValidationUtil.extractAndValidateUrlsFromRequest(headers)
        }
    }
}