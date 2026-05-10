package ru.home.project.ozonapi.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.http.codec.ClientCodecConfigurer
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono
import reactor.netty.http.client.HttpClient
import ru.home.project.ozonapi.config.properties.OzonApiProperties
import ru.home.project.ozonapi.exception.OzonException


/**
 * @author rlagay
 */
@Configuration
class ClientConfig {
    
    companion object {
        val log: Logger = LoggerFactory.getLogger(ClientConfig::class.java)
    }

    @Bean
    fun ozonClient(properties: OzonApiProperties) : WebClient {
        val mapper = ObjectMapper()
        mapper.registerModules(JavaTimeModule())
        val httpClient = HttpClient.create().wiretap(true)
        val strategies = ExchangeStrategies
            .builder()
            .codecs { clientDefaultCodecsConfigurer: ClientCodecConfigurer ->
                clientDefaultCodecsConfigurer.defaultCodecs().jackson2JsonEncoder(
                    Jackson2JsonEncoder(
                        mapper,
                        MediaType.APPLICATION_JSON
                    )
                )
                clientDefaultCodecsConfigurer.defaultCodecs().jackson2JsonDecoder(
                    Jackson2JsonDecoder(
                        mapper,
                        MediaType.APPLICATION_JSON
                    )
                )
                clientDefaultCodecsConfigurer.defaultCodecs().maxInMemorySize(10000 * 1024)
            }.build()
        return WebClient.builder()
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .exchangeStrategies(strategies)
            .baseUrl(properties.url)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .defaultHeader("Client-Id", properties.clientId)
            .defaultHeader("Api-Key", properties.apiKey)
            .defaultStatusHandler({ status: HttpStatusCode -> status.isError },
                { resp: ClientResponse ->
                    log.info(ObjectMapper().writeValueAsString(resp.bodyToMono<String>()))
                    Mono.error(
                        OzonException(
                            resp.bodyToMono<String>().toString(),
                            resp.statusCode().value()
                        )
                    )
                })
            .build()
    }
}