package com.fashiondigital.politicalspeeches.exception

import com.fashiondigital.politicalspeeches.model.ErrorMessageModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice


@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(EvaluationServiceException::class)
    fun handleException(ex: EvaluationServiceException): ResponseEntity<ErrorMessageModel> {
        val errorMessageModel: ErrorMessageModel = ErrorMessageModel(
                errorCode = ex.errorCode,
                errorMessage = ex.message)
        return ResponseEntity<ErrorMessageModel>(errorMessageModel, HttpStatus.BAD_REQUEST)
    }
}
