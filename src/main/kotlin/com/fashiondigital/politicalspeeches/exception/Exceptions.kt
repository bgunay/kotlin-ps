package com.fashiondigital.politicalspeeches.exception

import com.fashiondigital.politicalspeeches.model.ErrorCode


class EvaluationServiceException : RuntimeException {
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

class CsvParsingException : RuntimeException {
    val errorCode: ErrorCode

    constructor(errorCode: ErrorCode) : super(errorCode.value) {
        this.errorCode = errorCode
    }

}

class CsvPHttpException(val errorCode: ErrorCode) : RuntimeException(errorCode.value)
