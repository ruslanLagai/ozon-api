package ru.home.project.ozonapi.util

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import java.io.File


/**
 * @author rlagay
 */
fun <T> readResource(path: String, cl: Class<T>): T {
    val content = TestUtils::class.java.getClassLoader().getResource(path)
    return readValue(content.path, cl)
}

fun <T> readValue(resourcePath: String?, clazz: Class<T>?): T {
    val mapper = ObjectMapper()
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    mapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
    mapper.registerModule(JavaTimeModule())
    return mapper.readValue(File(resourcePath), clazz)
}

class TestUtils {



    fun <T> readValue(resourcePath: String?, clazz: Class<T>?): T {
        val mapper = ObjectMapper()
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        mapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
        mapper.registerModule(JavaTimeModule())
        return mapper.readValue(File(resourcePath), clazz)
    }
}