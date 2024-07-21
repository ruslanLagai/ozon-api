package ru.home.project.ozonapi.service.impl

import jakarta.annotation.PostConstruct
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
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

    @MockBean
    private lateinit var flywayConfig: FlywayConfig

    @MockBean
    private lateinit var telegramBot: TelegramBot

    @Autowired
    private lateinit var positionRepository: PositionRepository

    @Autowired
    private lateinit var ozonTotalRevenueCalculationServiceImpl: TotalRevenueCalculationService

    @Autowired
    private lateinit var yandexTotalRevenueCalculationServiceImpl: TotalRevenueCalculationService

    @PostConstruct
    fun init() {
        val umbrella1 = PositionEntity(1, "Мини зонт черный", 448.92, 11.69, "1135684591", "0000015")
        val umbrella2 = PositionEntity(2, "Мини зонт лавандовый", 448.92, 11.69, "1134671293", "0000009")
        val umbrella3 = PositionEntity(3, "Мини зонт голубой", 448.92, 11.69, "1134740183", "0000013")
        val umbrella4 = PositionEntity(4, "Мини зонт бежевый", 448.92, 11.69, "1134715033", "0000010")
        val umbrella5 = PositionEntity(5, "Мини зонт розовый", 448.92, 11.69, "1134731178", "0000011")
        val umbrella6 = PositionEntity(6, "Мини зонт серый", 448.92, 11.69, "1134733705", "0000012")
        val spongeHolder = PositionEntity(7, "Держатель для губки", 130.0, 3.0, "1075294535", "0000005", yandexArtikul = "0000005")
        val iceMaker = PositionEntity(8, "Форма для льда розовая", 70.0, 11.69, "1052327634", "manlrhmf2c9kn9qoo71i")
        val unknown = PositionEntity(9, "unknown", 437.15, 12.05, "105232763433", "00002")

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

    @Test
    fun `calculate yandex revenue`() {
        val request = RevenueRequest(null, null, null,
            OffsetDateTime.of(LocalDate.of(2024, 6, 1), LocalTime.MIN, ZoneOffset.ofHours(0)),
            OffsetDateTime.of(LocalDate.of(2024, 7, 30), LocalTime.MAX, ZoneOffset.ofHours(0))
        )

        val result = yandexTotalRevenueCalculationServiceImpl.calculateRevenue(request)
        assertEquals(1, result.size)
    }

}