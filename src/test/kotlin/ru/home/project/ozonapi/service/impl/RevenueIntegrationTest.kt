package ru.home.project.ozonapi.service.impl

import jakarta.annotation.PostConstruct
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
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
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneOffset

/**
 * @author rlagay
 */
@SpringBootTest
@Testcontainers
class RevenueIntegrationTest {

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

    @Autowired
    private lateinit var positionRepository: PositionRepository

    @Autowired
    private lateinit var calculationService: PositionRevenueCalculationServiceImpl

    @PostConstruct
    fun init() {
        val umbrella = PositionEntity(1, "Мини зонт", 437.15, 12.05, "1135684591", "0000015")
        val unknown = PositionEntity(2, "Мини зонтт", 437.15, 12.05, "11356845232334291", "0000015")
        positionRepository.save(umbrella)
        positionRepository.save(unknown)
    }

    @Test
    fun `calculate revenue no deliveries`() {
        val request = RevenueRequest("Мини зонтт", null, null,
            OffsetDateTime.of(LocalDate.of(2023, 10, 14), LocalTime.MIN, ZoneOffset.ofHours(0)),
            OffsetDateTime.of(LocalDate.of(2023, 10, 15), LocalTime.MAX, ZoneOffset.ofHours(0)))
        val result = calculationService.calculateRevenue(request)
        assertEquals("Отсутствуют доставки за выбранный период", result?.errorMessage)
        assertEquals("Мини зонтт", result?.name)
    }

    @Test
    fun `calculate revenue for umbrellas with suspicious deliveries`() {
        val request = RevenueRequest("Мини зонт", null, null,
            OffsetDateTime.of(LocalDate.of(2023, 10, 5), LocalTime.MIN, ZoneOffset.ofHours(0)),
            OffsetDateTime.of(LocalDate.of(2023, 10, 6), LocalTime.MAX, ZoneOffset.ofHours(0)))
        val result = calculationService.calculateRevenue(request)
        assertNull(result?.errorMessage)
        assertEquals("Мини зонт", result?.name)
        assertEquals(505.34, result?.averageRevenue)
        assertEquals(505.34, result?.totalRevenue)
        assertEquals(98.0, result?.taxes)
        assertEquals(0, result?.refundCount)
        assertEquals(1, result?.deliveryItemCount)
    }
}