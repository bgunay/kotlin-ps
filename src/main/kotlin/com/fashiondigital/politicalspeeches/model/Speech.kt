package com.fashiondigital.politicalspeeches.model

import java.time.LocalDate


data class Speech(
        val speaker: String,
        val topic: String,
        val date: LocalDate,
        val words: Int = 0
) {
    init {
        require(words > 0)
    }

    fun isSecurityTopic(securityTopic: String): Boolean {
        //supported case-insensitive "homeland security" "Homeland Security" ...
        return securityTopic.equals(topic, ignoreCase = true)
    }

    fun isTargetYear(targetYear: Int): Boolean {
        return targetYear == date.year
    }
}
