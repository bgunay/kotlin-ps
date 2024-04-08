package com.fashiondigital.politicalspeeches.exception

import com.fashiondigital.politicalspeeches.model.ErrorMessageModel
import com.fashiondigital.politicalspeeches.util.LoggerDelegate
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice


@RestControllerAdvice
class GlobalExceptionHandler {


    companion object {
        val log by LoggerDelegate()
    }

    @ExceptionHandler(EvaluationServiceException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private fun handleEvaluationServiceException(ex: EvaluationServiceException): ErrorMessageModel {
        val errorMessageModel = ErrorMessageModel(
            errorCode = ex.errorCode,
            errorMessage = ex.message
        )
        log.error("Error $errorMessageModel")
        return errorMessageModel
    }


    @ExceptionHandler(CsvParsingException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private fun handleCsvParseException(ex: CsvParsingException): ErrorMessageModel {
        val errorMessageModel = ErrorMessageModel(
            errorCode = ex.errorCode,
            errorMessage = ex.message
        )
        log.error("Error $errorMessageModel")
        return errorMessageModel
    }

    @ExceptionHandler(CsvPHttpException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private fun handleCsvHttpException(ex: CsvParsingException): ErrorMessageModel {
        val errorMessageModel = ErrorMessageModel(
            errorCode = ex.errorCode,
            errorMessage = ex.message
        )
        log.error("Error $errorMessageModel")
        return errorMessageModel
    }
}
