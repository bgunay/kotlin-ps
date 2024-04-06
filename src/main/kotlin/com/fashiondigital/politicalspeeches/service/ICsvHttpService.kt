package com.fashiondigital.politicalspeeches.service

import com.fashiondigital.politicalspeeches.model.Speech
import org.springframework.http.ResponseEntity

interface ICsvHttpService {
    suspend fun parseUrlsAndFetchCsvData(urls: Set<String>):List<String?>
}