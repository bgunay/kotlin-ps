package com.fashiondigital.politicalspeeches.model

import io.swagger.v3.oas.annotations.media.Schema

data class EvaluationResult(
    @Schema(description = "mostSpeeches", example = "Most Speech Politics")
    val mostSpeeches: String?,
    @Schema(description = "mostSecurity", example = "Most Secure Politics")
    val mostSecurity: String?,
    @Schema(description = "leastWordy", example = "Least Wordy Politics")
    val leastWordy: String?
)