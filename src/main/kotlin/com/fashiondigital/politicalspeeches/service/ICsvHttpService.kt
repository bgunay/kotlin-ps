package com.fashiondigital.politicalspeeches.service

interface ICsvHttpService {
    suspend fun parseUrlsAndFetchCsvData(urls: Set<String>): List<String?>
}