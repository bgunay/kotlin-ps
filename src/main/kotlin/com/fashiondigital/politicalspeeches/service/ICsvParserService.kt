package com.fashiondigital.politicalspeeches.service

import com.fashiondigital.politicalspeeches.model.Speech

interface ICsvParserService {
    fun parseCSV(csvData: List<String?>): List<Speech>
}