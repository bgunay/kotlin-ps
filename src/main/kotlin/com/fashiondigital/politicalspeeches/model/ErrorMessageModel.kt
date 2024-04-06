package com.fashiondigital.politicalspeeches.model

import java.time.LocalDateTime


data class ErrorMessageModel(
    val errorCode: ErrorCode?,
    val errorMessage: String?,
    val time: LocalDateTime = LocalDateTime.now()
)
