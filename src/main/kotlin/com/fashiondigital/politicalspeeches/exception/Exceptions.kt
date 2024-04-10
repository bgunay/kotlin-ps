package com.fashiondigital.politicalspeeches.exception

import com.fashiondigital.politicalspeeches.model.ErrorCode


class EvaluationServiceException : Exception {
    val errorCode: ErrorCode

    constructor(error: String?) : super(error) {
        this.errorCode = ErrorCode.GENERIC_ERROR
    }

    constructor(errorCode: ErrorCode) : super(errorCode.value) {
        this.errorCode = errorCode
    }

    constructor(errorCode: ErrorCode, ex: Throwable?) : super(errorCode.value, ex) {
        this.errorCode = errorCode
    }
}

class CsvParsingException(val errorCode: ErrorCode) : Exception(errorCode.value)


class CsvPHttpException(errorCode: ErrorCode, ex: Throwable?) : Exception(errorCode.value, ex)