package com.fashiondigital.politicalspeeches.validation

import com.fashiondigital.politicalspeeches.exception.CsvParsingException
import com.fashiondigital.politicalspeeches.exception.EvaluationServiceException
import com.fashiondigital.politicalspeeches.model.ErrorCode
import com.fashiondigital.politicalspeeches.model.Speech
import com.fashiondigital.politicalspeeches.model.constants.Constants
import com.fashiondigital.politicalspeeches.model.constants.Constants.URL_HEADER_PATTERN
import com.fashiondigital.politicalspeeches.util.LoggerDelegate
import org.apache.logging.log4j.util.Strings
import org.springframework.http.ResponseEntity
import java.net.URL


object ValidationUtil {

    private val log by LoggerDelegate()

    //no need to support file, ftp or jar
    fun extractAndValidateUrlsFromRequest(headers: Map<String, String>): Set<String> {
        val result: MutableSet<String> = HashSet()
        for (key in headers.keys) {
            if (!key.startsWith(URL_HEADER_PATTERN)) {
                throw EvaluationServiceException(ErrorCode.URL_PARAM_REQUIRED_ERROR)
            }
            if (isValidURL(headers[key])) {
                result.add(headers[key]!!)
            }
        }
        if (result.isEmpty()) {
            throw EvaluationServiceException(ErrorCode.URL_PARAM_REQUIRED_ERROR)
        }
        return result
    }


    private fun isValidURL(url: String?): Boolean {
        if (url == null) {
            throw EvaluationServiceException(ErrorCode.UNSUPPORTED_PROTOCOL)
        }
        return try {
            val uri = URL(url).toURI()
            if (!Constants.SUPPORTED_PROTOCOLS.contains(uri.scheme)) {
                throw EvaluationServiceException(ErrorCode.UNSUPPORTED_PROTOCOL)
            }
            true
        } catch (ex: Exception) {
            log.error(ErrorCode.UNSUPPORTED_PROTOCOL.value, ex)
            when (ex) {
                is EvaluationServiceException -> throw EvaluationServiceException(ErrorCode.UNSUPPORTED_PROTOCOL)
                else -> throw EvaluationServiceException(ErrorCode.URL_VALIDATION_ERROR, ex)
            }
        }
    }


    fun validateSpeech(speech: Speech) {
        if (speech.wordCount < 0) {
            throw CsvParsingException(ErrorCode.MINUS_WORD_ERROR)
        }
        if (Strings.isEmpty(speech.topic)) {
            throw CsvParsingException(ErrorCode.TOPIC_MISSING)
        }
        if (Strings.isEmpty(speech.speaker)) {
            throw CsvParsingException(ErrorCode.SPEAKER_MISSING)
        }
    }


    fun checkCsvResponseValid(response: ResponseEntity<String>?) {
        if (!response!!.hasBody() || Strings.isEmpty(response.body)) {
            throw CsvParsingException(ErrorCode.CSV_EMPTY_BODY_ERROR)
        }
        if (response.hasBody() && response.body!!.contains(",")) {
            throw CsvParsingException(ErrorCode.WRONG_DELIMITER_CSV)
        }

    }


}