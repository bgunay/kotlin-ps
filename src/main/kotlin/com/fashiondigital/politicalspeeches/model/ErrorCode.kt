package com.fashiondigital.politicalspeeches.model


enum class ErrorCode(val value: String) {
    CSV_PARSER_ERROR("Failed to parse csv file."),
    URL_READER_ERROR("Failed to read url."),
    URL_VALIDATION_ERROR("Failed to parse url."),
    UNSUPPORTED_PROTOCOL("Supported protocols are http and https."),
    URL_PARAM_REQUIRED_ERROR("Url query param is required like url1=url&url2=url...");
}
