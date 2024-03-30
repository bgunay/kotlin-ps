package com.fashiondigital.politicalspeeches.validation

import com.fashiondigital.politicalspeeches.exception.EvaluationServiceException
import com.fashiondigital.politicalspeeches.model.ErrorCode
import com.fashiondigital.politicalspeeches.model.constants.Constants
import java.net.URL


object UrlHeaderValidation {
    //no need to support file, ftp or jar

    fun extractAndValidateUrlsFromRequest(headers: Map<String, String>): Set<String> {
        val result: MutableSet<String> = HashSet()
        for (key in headers.keys) {
            val m = Constants.URL_HEADER_PATTERN.matcher(key)
            if (m.find() && isValidURL(headers[key])) {
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
        } catch (e: Exception) {
            when (e) {
                is EvaluationServiceException -> throw EvaluationServiceException(ErrorCode.UNSUPPORTED_PROTOCOL)
                else -> throw EvaluationServiceException(ErrorCode.URL_VALIDATION_ERROR, e)
            }
        }
    }
}
