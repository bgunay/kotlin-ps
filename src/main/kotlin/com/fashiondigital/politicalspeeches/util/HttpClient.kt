package com.fashiondigital.politicalspeeches.util

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import com.fashiondigital.politicalspeeches.exception.DownloadException
import io.ktor.http.*
import org.springframework.stereotype.Component

@Component
class HttpClient {
    private val client = HttpClient(CIO)

    suspend fun downloadCsv(url: String): String {
        try {
            val response: HttpResponse = client.get(url)
            if (response.status.isSuccess()) {
                return response.bodyAsText()
            } else {
                throw DownloadException("Failed to download CSV from $url - Status: ${response.status}")
            }
        } catch (e: Exception) {
            throw DownloadException("Error occurred while making HTTP request: ${e.message}")
        }
    }
}
