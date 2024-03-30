package com.fashiondigital.politicalspeeches.model

import java.time.LocalDate


data class Speech(
        val speaker: String,
        val topic: String,
        val date: LocalDate,
        val wordCount: Int = 0
) {
    init {
        require(wordCount > 0)
    }

}
