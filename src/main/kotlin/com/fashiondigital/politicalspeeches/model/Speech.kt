package com.fashiondigital.politicalspeeches.model

import java.time.LocalDate


data class Speech(
    val speaker: String,
    val topic: String,
    val date: LocalDate,
    var wordCount: Int = 0,
) {
}