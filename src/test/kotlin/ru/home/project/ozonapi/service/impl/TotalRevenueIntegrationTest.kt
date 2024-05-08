package ru.home.project.ozonapi.service.impl

import jakarta.annotation.PostConstruct
import org.junit.jupiter.api.Assertions.assertEquals
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

    @Autowired
    private lateinit var positionRepository: PositionRepository

    @Autowired
    private lateinit var calculationService: TotalRevenueCalculationService

    @PostConstruct
    fun init() {
        val umbrella1 = PositionEntity(1, "Мини зонт черный", 448.92, 11.69, "1135684591", "0000015")
        val umbrella2 = PositionEntity(2, "Мини зонт лавандовый", 448.92, 11.69, "1134671293", "0000009")
        val umbrella3 = PositionEntity(3, "Мини зонт голубой", 448.92, 11.69, "1134740183", "0000013")
        val umbrella4 = PositionEntity(4, "Мини зонт бежевый", 448.92, 11.69, "1134715033", "0000010")
        val umbrella5 = PositionEntity(5, "Мини зонт розовый", 448.92, 11.69, "1134731178", "0000011")
        val umbrella6 = PositionEntity(6, "Мини зонт серый", 448.92, 11.69, "1134733705", "0000012")
        val spongeHolder = PositionEntity(7, "Держатель для губки", 76.22, 11.69, "1075294535", "0000005")
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
        val result = calculationService.calculateRevenue(request)
        assertEquals(9, result.size)

        // черный зонт
        val umbrella1 = result.first { it.name == "Мини зонт черный" }
        assertEquals(538.92, umbrella1.averageRevenue)
        assertEquals(538.92, umbrella1.totalRevenue)
        assertEquals(84.0, umbrella1.taxes)
        assertEquals(-2217.1, umbrella1.marketingCosts)
        assertEquals(29, umbrella1.soldItemsCount)
        assertEquals(0, umbrella1.refundCount)

        // лавандовый зонт
        val umbrella2 = result.first { it.name == "Мини зонт лавандовый" }
        assertEquals(492.8, umbrella2.averageRevenue)
        assertEquals(4927.99, umbrella2.totalRevenue)
        assertEquals(804.12, umbrella2.taxes)
        assertEquals(-2217.1, umbrella2.marketingCosts)
        assertEquals(29, umbrella2.soldItemsCount)
        assertEquals(0, umbrella2.refundCount)

        // голубой зонт
        val umbrella3 = result.first { it.name == "Мини зонт голубой" }
        assertEquals(441.92, umbrella3.averageRevenue)
        assertEquals(883.83, umbrella3.totalRevenue)
        assertEquals(152.28, umbrella3.taxes)
        assertEquals(-2217.1, umbrella3.marketingCosts)
        assertEquals(29, umbrella3.soldItemsCount)
        assertEquals(0, umbrella3.refundCount)

        // бежевый зонт CHECK
        val umbrella4 = result.first { it.name == "Мини зонт бежевый" }
        assertEquals(505.58, umbrella4.averageRevenue)
        assertEquals(1516.74, umbrella4.totalRevenue)
        assertEquals(243.0, umbrella4.taxes)
        assertEquals(-2217.1, umbrella4.marketingCosts)
        assertEquals(29, umbrella4.soldItemsCount)
        assertEquals(0, umbrella4.refundCount)

        // розовый зонт
        val umbrella5 = result.first { it.name == "Мини зонт розовый" }
        assertEquals(446.22, umbrella5.averageRevenue)
        assertEquals(892.43, umbrella5.totalRevenue)
        assertEquals(153.9, umbrella5.taxes)
        assertEquals(-2217.1, umbrella5.marketingCosts)
        assertEquals(29, umbrella5.soldItemsCount)
        assertEquals(0, umbrella5.refundCount)

        // серый зонт CHECK
        val umbrella6 = result.first { it.name == "Мини зонт серый" }
        assertEquals(63.95, umbrella6.averageRevenue)
        assertEquals(652.45, umbrella6.totalRevenue)
        assertEquals(162.0, umbrella6.taxes)
        assertEquals(-2217.1, umbrella6.marketingCosts)
        assertEquals(29, umbrella6.soldItemsCount)
        assertEquals(2, umbrella6.refundCount)

        // губка
        val spongeHolder = result.first { it.name == "Держатель для губки" }
        assertEquals(130.52, spongeHolder.averageRevenue)
        assertEquals(1044.16, spongeHolder.totalRevenue)
        assertEquals(181.32, spongeHolder.taxes)
        assertEquals(-2217.1, spongeHolder.marketingCosts)
        assertEquals(29, spongeHolder.soldItemsCount)
        assertEquals(0, spongeHolder.refundCount)

    }

}