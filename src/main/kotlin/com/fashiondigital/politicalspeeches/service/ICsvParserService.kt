package com.fashiondigital.politicalspeeches.service

import com.fashiondigital.politicalspeeches.model.Speech

interface ICsvParserService {
    fun parseCSVsByUrls(urls: Set<String>): List<Speech>
}