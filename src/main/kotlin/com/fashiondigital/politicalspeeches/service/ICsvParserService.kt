package com.fashiondigital.politicalspeeches.service

import com.fashiondigital.politicalspeeches.model.Speech

interface ICsvParserService {
    suspend fun parseCSVsByUrls(urls: List<String>): List<Speech>
}