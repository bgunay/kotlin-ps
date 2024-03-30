package com.fashiondigital.politicalspeeches.util

import org.apache.commons.csv.CSVFormat

object CSVUtil {

    private const val DELIMITER = ";"

    fun setCVSFormat(): CSVFormat? {
        val csvFormat = CSVFormat.DEFAULT.builder().setHeader()
            .setSkipHeaderRecord(true)
            .setIgnoreHeaderCase(true)
            .setIgnoreSurroundingSpaces(true)
            .setIgnoreEmptyLines(true)
            .setDelimiter(DELIMITER)
            .build()
        return csvFormat
    }
}