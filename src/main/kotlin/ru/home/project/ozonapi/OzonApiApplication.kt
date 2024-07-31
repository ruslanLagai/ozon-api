package ru.home.project.ozonapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableCaching
@EnableScheduling
@ConfigurationPropertiesScan
class OzonApiApplication

fun main(args: Array<String>) {
	runApplication<OzonApiApplication>(*args)
}
