package ru.home.project.ozonapi.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * @author rlagay
 */
@ConfigurationProperties("service.ozon.api")
data class OzonApiProperties(val url: String, val clientId: String, val apiKey: String)
