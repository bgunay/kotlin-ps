package com.fashiondigital.politicalspeeches.exception

import com.fashiondigital.politicalspeeches.model.ErrorCode

class CsvParsingException : RuntimeException {
    val errorCode: ErrorCode

    constructor(errorCode: ErrorCode) : super(errorCode.value) {
        this.errorCode = errorCode
    }

    constructor(errorCode: ErrorCode, ex: Throwable?) : super(errorCode.value, ex) {
        this.errorCode = errorCode
    }
}


class EvaluationServiceException : RuntimeException {
    val errorCode: ErrorCode

    constructor(errorCode: ErrorCode) : super(errorCode.value) {
        this.errorCode = errorCode
    }

    constructor(errorCode: ErrorCode, ex: Throwable?) : super(errorCode.value, ex) {
        this.errorCode = errorCode
    }
}

class DownloadException(message: String) : RuntimeException(message)