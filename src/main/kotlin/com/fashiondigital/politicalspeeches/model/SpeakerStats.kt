package com.fashiondigital.politicalspeeches.model

//Assumed overall words cannot exceed the int range
data class SpeakerStats(var targetYearSpeeches: Int, var securitySpeeches: Int, var overallWords: Int) {

    fun merge(other: SpeakerStats?): SpeakerStats {
        if (other == null) {
            return this
        }
        targetYearSpeeches += other.targetYearSpeeches
        securitySpeeches += other.securitySpeeches
        overallWords += other.overallWords
        return this
    }
}
