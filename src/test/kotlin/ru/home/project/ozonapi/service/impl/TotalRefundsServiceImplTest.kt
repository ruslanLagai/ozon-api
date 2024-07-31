package ru.home.project.ozonapi.service.impl

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
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
import ru.home.project.ozonapi.service.TotalRefundsService
import java.time.OffsetDateTime
import java.time.ZoneOffset

/**
 * @author rlagay
 */
@SpringBootTest
@Testcontainers
class TotalRefundsServiceImplTest {

    companion object {
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
            registry.add("spring.flyway.schemas", container::getDatabaseName)
            registry.add("spring.data.redis.host", redisContainer::getHost)
            registry.add("spring.data.redis.port", redisContainer::getFirstMappedPort)

        }
    }

    @Autowired
    private lateinit var totalRefundsService: TotalRefundsService

    @Test
    fun `test refund data for all positions`() {
        val from = OffsetDateTime.of(2024, 4, 1, 0, 0, 0, 0, ZoneOffset.UTC)
        val to = OffsetDateTime.of(2024, 4, 15, 0, 0, 0, 0, ZoneOffset.UTC)

        val result = totalRefundsService.getRefundsData(from, to)

        assertEquals(24, result.totalRefundsCount)
        assertEquals(24, result.totalRefundsDeliveredCount)
        assertEquals(0, result.totalRefundsToBeDeliveredCount)
        assertEquals(7, result.refundsData.size)
        assertEquals(1, result.refundsData.find { it.name.contains("черный") }!!.refundCount)
        assertEquals(2, result.refundsData.find { it.name.contains("серые") }!!.refundCount)
        assertEquals(6, result.refundsData.find { it.name.contains("лавандовый") }!!.refundCount)
        assertEquals(2, result.refundsData.find { it.name.contains("серый") }!!.refundCount)
        assertEquals(1, result.refundsData.find { it.name.contains("голубой") }!!.refundCount)
        assertEquals(7, result.refundsData.find { it.name.contains("бежевый") }!!.refundCount)
        assertEquals(5, result.refundsData.find { it.name.contains("розовый") }!!.refundCount)
    }

    @Test
    fun `test refund data by clusters`() {
        val from = OffsetDateTime.of(2024, 4, 1, 0, 0, 0, 0, ZoneOffset.UTC)
        val to = OffsetDateTime.of(2024, 4, 15, 0, 0, 0, 0, ZoneOffset.UTC)

        val result = totalRefundsService.getRefundsDataByClusters(from, to)

        assertEquals(24, result.totalRefundsCount)
        assertEquals(24, result.totalRefundsDeliveredCount)
        assertEquals(0, result.totalRefundsToBeDeliveredCount)
        assertEquals(14, result.refundsByClusterData.size)
        assertEquals(6, result.itemByCluster.size)
        assertEquals(1, result.itemByCluster["Калининград"]!!.totalRefundsCount)
        assertEquals(5, result.itemByCluster["Москва-Восток и Дальние регионы"]!!.totalRefundsCount)
        assertEquals(11, result.itemByCluster["Санкт-Петербург и СЗО"]!!.totalRefundsCount)
        assertEquals(1, result.itemByCluster["Дальний Восток"]!!.totalRefundsCount)
        assertEquals(4, result.itemByCluster["Поволжье"]!!.totalRefundsCount)
    }
}