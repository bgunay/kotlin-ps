package com.fashiondigital.politicalspeeches

import org.apache.commons.io.IOUtils
import java.io.IOException
import java.nio.charset.StandardCharsets


object TestUtils {
    @Throws(IOException::class)
    fun getResourceContent(path: String): String {
        return IOUtils.toString(TestUtils::class.java.getClassLoader().getResourceAsStream(path),
                StandardCharsets.UTF_8.name())
    }
}
