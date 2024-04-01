package com.fashiondigital.politicalspeeches.model


enum class ErrorCode(val value: String) {
    MINUS_WORD_ERROR("Word count can not be minus."),
    WRONG_DELIMITER_CSV("Wrong delimiter used, use ';'."),
    CSV_EMPTY_BODY_ERROR("Fetched CSV is empty!"),
    URL_READER_ERROR("Failed to read url."),
    URL_VALIDATION_ERROR("Failed to parse url."),
    UNSUPPORTED_PROTOCOL("Supported protocols are http and https."),
    URL_PARAM_REQUIRED_ERROR("Url query param is required like url1=url&url2=url..."),
    SPEAKER_MISSING("Speaker is missing"),
    TOPIC_MISSING("Topic is missing"),
    GENERIC_ERROR("Generic Error, see logs for details"),
    FETCH_CSV_TIMEOUT("Timeout exceeded, please try again")
}
