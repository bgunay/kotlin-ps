package com.fashiondigital.politicalspeeches.service.impl

import com.fashiondigital.politicalspeeches.model.SpeakerStats

interface ICsvParserService {
    fun parseCSVsByUrls(urls: Set<String>): Map<String, SpeakerStats>
}