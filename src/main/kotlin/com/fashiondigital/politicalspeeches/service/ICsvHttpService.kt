package com.fashiondigital.politicalspeeches.service

interface ICsvHttpService {
    fun parseUrlsAndFetchCsvData(urls: Set<String>): List<String?>
}