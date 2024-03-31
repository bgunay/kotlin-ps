package com.fashiondigital.politicalspeeches.validation

import com.fashiondigital.politicalspeeches.exception.EvaluationServiceException
import com.fashiondigital.politicalspeeches.model.ErrorCode
import com.fashiondigital.politicalspeeches.model.constants.Constants
import org.apache.commons.csv.CSVRecord
import org.apache.logging.log4j.util.Strings
import org.springframework.http.ResponseEntity
import java.net.URL


object ValidationUtil {
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

    fun checkCsvValid(response: ResponseEntity<String?>): Boolean {
        if (!response.hasBody() || Strings.isEmpty(response.body)) {
            throw EvaluationServiceException(ErrorCode.CSV_EMPTY_BODY_ERROR)
        }
        if(response.hasBody() && response.body!!.contains(",")  ){
            throw EvaluationServiceException(ErrorCode.CSV_PARSER_ERROR)
        }

        return true
    }

    fun checkWordCounts(records: List<CSVRecord>) {
        records.forEach {
            val wordCount = it.get("Words").toInt()
            if (wordCount < 0) {
                throw EvaluationServiceException(ErrorCode.CSV_PARSER_ERROR)
            }
        }
    }

}
