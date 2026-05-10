package ru.home.project.ozonapi.service.impl

import jakarta.annotation.PostConstruct
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import ru.home.project.ozonapi.config.FlywayConfig
import ru.home.project.ozonapi.dto.request.RevenueRequest
import ru.home.project.ozonapi.entity.PositionEntity
import ru.home.project.ozonapi.repository.PositionRepository
import ru.home.project.ozonapi.service.TotalRevenueCalculationService
import ru.home.project.ozonapi.telegram.TelegramBot
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneOffset

/**
 * @author rlagay
 */
@SpringBootTest
@Testcontainers
class TotalRevenueIntegrationTest {

    companion object App {
        @Container
        protected var container: MySQLContainer<*> = MySQLContainer("mysql:8")

        @Container
        protected var redisContainer: GenericContainer<Nothing> = GenericContainer<Nothing>(DockerImageName.parse("redis:5.0.3-alpine"))
            .withExposedPorts(6379)

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", container::getJdbcUrl)
            registry.add("spring.datasource.password", container::getPassword)
            registry.add("spring.datasource.username", container::getUsername)
            registry.add("spring.data.redis.host", redisContainer::getHost)
            registry.add("spring.data.redis.port", redisContainer::getFirstMappedPort)
        }
    }

    @MockitoBean
    private lateinit var flywayConfig: FlywayConfig

    @MockitoBean
    private lateinit var telegramBot: TelegramBot

    @Autowired
    private lateinit var positionRepository: PositionRepository

    @Autowired
    private lateinit var ozonTotalRevenueCalculationServiceImpl: TotalRevenueCalculationService

    @PostConstruct
    fun init() {
        val umbrella1 = PositionEntity(name = "Мини зонт черный", costPrice = 448.92, additionalCost = 11.69, ozonId = "1135684591", artikul = "0000015")
        val umbrella2 = PositionEntity(name = "Мини зонт лавандовый", costPrice = 448.92, additionalCost = 11.69, ozonId = "1134671293", artikul = "0000009")
        val umbrella3 = PositionEntity(name = "Мини зонт голубой", costPrice = 448.92, additionalCost = 11.69, ozonId = "1134740183", artikul = "0000013")
        val umbrella4 = PositionEntity(name = "Мини зонт бежевый", costPrice = 448.92, additionalCost = 11.69, ozonId = "1134715033", artikul = "0000010")
        val umbrella5 = PositionEntity(name = "Мини зонт розовый", costPrice = 448.92, additionalCost = 11.69, ozonId = "1134731178", artikul = "0000011")
        val umbrella6 = PositionEntity(name = "Мини зонт серый", costPrice = 448.92, additionalCost = 11.69, ozonId = "1134733705", artikul = "0000012")
        val spongeHolder = PositionEntity(name = "Держатель для губки", costPrice = 130.0, additionalCost = 3.0, ozonId = "1075294535", artikul = "0000005", yandexArtikul = "0000005")
        val iceMaker = PositionEntity(name = "Форма для льда розовая", costPrice = 70.0, additionalCost = 11.69, ozonId = "1052327634", artikul = "manlrhmf2c9kn9qoo71i")
        val unknown = PositionEntity(name = "unknown", costPrice = 437.15, additionalCost = 12.05, ozonId = "105232763433", artikul = "00002")

        positionRepository.save(umbrella1)
        positionRepository.save(umbrella2)
        positionRepository.save(umbrella3)
        positionRepository.save(umbrella4)
        positionRepository.save(umbrella5)
        positionRepository.save(umbrella6)
        positionRepository.save(spongeHolder)
        positionRepository.save(iceMaker)
        positionRepository.save(unknown)
    }

    @Test
    fun `calculate revenue`() {
        val request = RevenueRequest(null, null, null,
            OffsetDateTime.of(LocalDate.of(2023, 10, 5), LocalTime.MIN, ZoneOffset.ofHours(0)),
            OffsetDateTime.of(LocalDate.of(2023, 10, 6), LocalTime.MAX, ZoneOffset.ofHours(0)))
        val result = ozonTotalRevenueCalculationServiceImpl.calculateRevenue(request)
        assertEquals(9, result.size)
    }
}