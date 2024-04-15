package com.fashiondigital.politicalspeeches.service

interface ICsvHttpService {
    suspend fun fetchCsvData(urls: Set<String>): List<String?>
    suspend fun fetchCsvDataWithFlow(urls: Set<String>): List<String?>
}