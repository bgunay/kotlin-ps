package com.fashiondigital.politicalspeeches

import java.util.regex.Pattern

object Constants {
    val SUPPORTED_PROTOCOLS = setOf("http", "https")
    val URL_HEADER_PATTERN: Pattern = Pattern.compile("url\\d+")

}