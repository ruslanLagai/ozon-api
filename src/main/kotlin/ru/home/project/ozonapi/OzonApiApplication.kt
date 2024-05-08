package ru.home.project.ozonapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
@EnableCaching
@ConfigurationPropertiesScan
class OzonApiApplication

fun main(args: Array<String>) {
	runApplication<OzonApiApplication>(*args)
}
