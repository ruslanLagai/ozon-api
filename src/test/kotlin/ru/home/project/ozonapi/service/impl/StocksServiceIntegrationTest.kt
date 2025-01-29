package ru.home.project.ozonapi.service.impl

import jakarta.annotation.PostConstruct
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
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
import ru.home.project.ozonapi.entity.ChinaOrderEntity
import ru.home.project.ozonapi.entity.ChinaStockEntity
import ru.home.project.ozonapi.entity.StockEntity
import ru.home.project.ozonapi.repository.ChinaOrdersRepository
import ru.home.project.ozonapi.repository.OzonSupplyRepository
import ru.home.project.ozonapi.repository.StockRepository
import ru.home.project.ozonapi.service.StocksService
import java.time.LocalDate

/**
 * @author rlagay
 */
@SpringBootTest
@Testcontainers
class StocksServiceIntegrationTest {

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
    private lateinit var chinaOrdersRepository: ChinaOrdersRepository

    @Autowired
    private lateinit var stockService: StocksService

    @Autowired
    private lateinit var stockRepository: StockRepository

    @Autowired
    private lateinit var posiRepository: StockRepository

    @MockBean
    private lateinit var ozonSupplyRepository: OzonSupplyRepository

    @PostConstruct
    fun init() {
        val umbrellas = listOf(
            ChinaStockEntity(name = "Мини зонт лавандовый", price = 19.8, priceRub = 277.2, artikul = "0000009", quantity = 40, ozonId = "1134671293"),
            ChinaStockEntity(name = "Мини зонт черный", price = 19.8, priceRub = 277.2, artikul = "0000015", quantity = 50, ozonId = "1135684591"),
            ChinaStockEntity(name = "Мини зонт серый", price = 19.8, priceRub = 277.2, artikul = "0000012", quantity = 10, ozonId = "1134733705")
        )
        val umbrellaOrder = ChinaOrderEntity(supplier = "GOMARKT", delivered = false, mass = 40.0, volume = 0.4, stockCost = 30759.0, products = umbrellas, orderDate = LocalDate.now())

        val hanger = listOf(
            ChinaStockEntity(name = "Вешалки плечики, серые", price = 7.2, priceRub = 100.8, artikul = "0000027", quantity = 100, ozonId = "1368971009")
        )
        val hangerOrder = ChinaOrderEntity(supplier = "GOMARKT", delivered = false, mass = 80.0, volume = 0.4, stockCost = 11977.0, products = hanger, orderDate = LocalDate.now())

        chinaOrdersRepository.save(umbrellaOrder)
        chinaOrdersRepository.save(hangerOrder)
        posiRepository.findAll().forEach {
            val stock = stockRepository.getByOzonId(it.ozonId)
            if (stock == null) {
                stockRepository.save(
                    StockEntity(
                        artikul = it.artikul,
                        name = it.name,
                        ozonId = it.ozonId,
                        quantity = 1000,
                        yandexArtikul = it.yandexArtikul
                    )
                )
            } else {
                stockRepository.updateQuantityByOzonId(it.ozonId, stock.quantity + 1000)
            }
        }
    }

    @Test
    fun `test worth stocks calculation`() {
        val result = stockService.getStocks()

        assertEquals(2, result.orders.size)
        assertEquals(42736.0, result.stocksOnWayWorth)
        assertTrue(result.products.isNotEmpty())
        assertTrue(result.stocksWorth > 0.0)
        assertTrue(result.deliveryWorth > 0.0)
    }
}