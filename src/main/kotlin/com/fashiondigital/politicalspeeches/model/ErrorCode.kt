package com.fashiondigital.politicalspeeches.model


enum class ErrorCode(val value: String) {
    CSV_PARSER_ERROR("Failed to parse csv file."),
    CSV_EMPTY_BODY_ERROR("Fetched CSV is empty!"),
    URL_READER_ERROR("Failed to read url."),
    URL_VALIDATION_ERROR("Failed to parse url."),
    UNSUPPORTED_PROTOCOL("Supported protocols are http and https."),
    URL_PARAM_REQUIRED_ERROR("Url query param is required like url1=url&url2=url..."),
    INVALID_NUMBER_FORMAT("Invalid number format in CSV data"),
    INVALID_DATE_FORMAT("Invalid date format in CSV data"),
    SPEAKER_MISSING_ERROR("Speaker is missing"),
    TOPIC_MISSING_ERROR("Topic is missing"),
    WORD_COUNT_ERROR("Words count is missing");
}
