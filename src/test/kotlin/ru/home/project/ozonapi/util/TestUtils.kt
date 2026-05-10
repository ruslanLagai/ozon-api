package ru.home.project.ozonapi.util

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.io.File
import java.math.BigDecimal


/**
 * @author rlagay
 */
fun <T> readResource(path: String, cl: Class<T>): T {
    val content = TestUtils::class.java.getClassLoader().getResource(path)
    return readValue(content.path, cl)
}

fun readResource(path: String): String {
    val content = TestUtils::class.java.getClassLoader().getResource(path)
    val file = File(content!!.path)
    return file.readText(Charsets.UTF_8)
}

fun <T> readValue(resourcePath: String?, clazz: Class<T>?): T {
    val mapper = ObjectMapper()
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    mapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
    mapper.registerModule(JavaTimeModule())
    return mapper.readValue(File(resourcePath), clazz)
}

object BigDecimalAdapter {
    @FromJson
    fun fromJson(string: String) = BigDecimal(string)

    @ToJson
    fun toJson(value: BigDecimal) = value.toString()
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