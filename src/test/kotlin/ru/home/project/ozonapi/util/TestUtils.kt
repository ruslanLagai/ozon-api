package ru.home.project.ozonapi.util

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonReader
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okio.BufferedSource
import org.openapitools.client.infrastructure.LocalDateAdapter
import org.openapitools.client.infrastructure.OffsetDateTimeAdapter
import org.openapitools.client.models.GetOrdersResponse
import java.io.File
import java.math.BigDecimal
import java.nio.charset.Charset


/**
 * @author rlagay
 */
fun <T> readResource(path: String, cl: Class<T>): T {
    val content = TestUtils::class.java.getClassLoader().getResource(path)
    return readValue(content.path, cl)
}

fun <T> readResourceMoshi(path: String, cl: Class<T>): T {
    val content = TestUtils::class.java.getClassLoader().getResource(path)
    return readValueMoshi(content.path, cl)
}

fun <T> readValue(resourcePath: String?, clazz: Class<T>?): T {
    val mapper = ObjectMapper()
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    mapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
    mapper.registerModule(JavaTimeModule())
    return mapper.readValue(File(resourcePath), clazz)
}

fun <T> readValueMoshi(resourcePath: String?, clazz: Class<T>?): T {
    val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .add(BigDecimalAdapter)
        .add(LocalDateAdapter())
        .add(OffsetDateTimeAdapter())
        .build()
    val jsonAdapter = clazz?.let { moshi.adapter(it) }

    return jsonAdapter!!.fromJson(File(resourcePath).inputStream().readBytes().toString(Charset.defaultCharset()))!!
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