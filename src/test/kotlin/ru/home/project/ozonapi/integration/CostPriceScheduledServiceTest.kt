package ru.home.project.ozonapi.integration

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.matching.ContainsPattern
import jakarta.persistence.EntityManager
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.jdbc.Sql
import org.springframework.transaction.support.TransactionTemplate
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import org.wiremock.spring.ConfigureWireMock
import org.wiremock.spring.EnableWireMock
import org.wiremock.spring.InjectWireMock
import ru.home.project.ozonapi.config.FlywayConfig
import ru.home.project.ozonapi.repository.ChinaOrdersRepository
import ru.home.project.ozonapi.repository.OzonSupplyOrderIdRepository
import ru.home.project.ozonapi.repository.PositionRepository
import ru.home.project.ozonapi.repository.TransactionRepository
import ru.home.project.ozonapi.scheduled.CostPriceScheduledService
import ru.home.project.ozonapi.service.AdditionalServicesForCostPriceService
import ru.home.project.ozonapi.service.CrossDocService
import ru.home.project.ozonapi.service.impl.ChinaOrdersService
import ru.home.project.ozonapi.telegram.TelegramBot
import ru.home.project.ozonapi.util.readResource

@SpringBootTest(
    properties = [
        "service.ozon.api.url=\${wiremock.server.baseUrl}"
    ]
)
@Testcontainers
@EnableWireMock
@Sql(value = [
    "/sql/init-db.sql", "/sql/position_entity.sql"
])
@ConfigureWireMock(baseUrlProperties = ["service.ozon.api.url"], name = "ozon")
class CostPriceScheduledServiceTest {

    companion object App {
        @Container
        private var container: MySQLContainer<*> = MySQLContainer("mysql:8")

        @Container
        private var redisContainer: GenericContainer<Nothing> = GenericContainer<Nothing>(DockerImageName.parse("redis:5.0.3-alpine"))
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

    @InjectWireMock
    private lateinit var wireMock: WireMockServer

    @MockitoBean
    private lateinit var flywayConfig: FlywayConfig

    @MockitoBean
    private lateinit var telegramBot: TelegramBot

    @Autowired
    private lateinit var positionRepository: PositionRepository

    @Autowired
    private lateinit var fulfilmentAdditionalService: AdditionalServicesForCostPriceService

    @Autowired
    private lateinit var chinaOrdersRepository: ChinaOrdersRepository

    @Autowired
    private lateinit var ozonSupplyRepository: OzonSupplyOrderIdRepository

    @Autowired
    private lateinit var transactionsRepository: TransactionRepository

    @Autowired
    private lateinit var entityManager: EntityManager

    @Autowired
    private lateinit var transactionTemplate: TransactionTemplate

    @Autowired
    private lateinit var crossDocService: CrossDocService

    @Autowired
    private lateinit var chinaOrdersService: ChinaOrdersService

    @BeforeEach
    fun before() {

    }

    @Autowired
    private lateinit var costPriceScheduledService: CostPriceScheduledService

    private val transactionsOne = readResource("transactions/cross-doc/transactions-page-1.json")
    private val transactionsTwo = readResource("transactions/cross-doc/transactions-page-2.json")
    private val transactionsThree = readResource("transactions/cross-doc/transactions-page-3.json")
    private val transactionsFour = readResource("transactions/cross-doc/transactions-page-4.json")
    private val getSingleSupplyData = readResource("cross-doc/get-single-supply-orders.json")
    private val getMultiSupplyData = readResource("cross-doc/get-multi-supply-orders.json")
    private val getTotalSupplyData = readResource("cross-doc/get-supply-orders-nes.json")

    private val supplyBundleMulti = readResource("cross-doc/supply-bundle-for-multi-supply.json")
    private val supplyBundleSignle = readResource("cross-doc/supply-bundle-3.json")

    @Test
    fun `test link transactions with cost price entity`() {

        stubFor(post("/v3/finance/transaction/list")
            .withRequestBody(ContainsPattern("\"page\":1,"))
            .willReturn(ok(transactionsOne)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)))
        stubFor(post("/v3/finance/transaction/list")
            .withRequestBody(ContainsPattern("\"page\":2,"))
            .willReturn(ok(transactionsTwo)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)))
        stubFor(post("/v3/finance/transaction/list")
            .withRequestBody(ContainsPattern("\"page\":3,"))
            .willReturn(ok(transactionsThree)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)))
        stubFor(post("/v3/finance/transaction/list")
            .withRequestBody(ContainsPattern("\"page\":4,"))
            .willReturn(ok(transactionsFour)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)))
        stubFor(post("/v3/supply-order/list")
            .willReturn(ok("""
                {
                    "order_ids": [
                        95031987,
                        100667309
                    ],
                    "last_id": 0
                }
            """.trimIndent())
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)))
        stubFor(post("/v3/supply-order/get")
            .withRequestBody(ContainsPattern("\"order_ids\":[95031987]"))
            .willReturn(ok(getSingleSupplyData)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)))
        stubFor(post("/v3/supply-order/get")
            .withRequestBody(ContainsPattern("\"order_ids\":[100667309]"))
            .willReturn(ok(getMultiSupplyData)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)))
        stubFor(post("/v3/supply-order/get")
            .withRequestBody(ContainsPattern("\"order_ids\":[95031987,100667309]"))
            .willReturn(ok(getTotalSupplyData)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)))

        stubFor(post("/v1/supply-order/bundle")
            .willReturn(ok(supplyBundleMulti)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)))
        stubFor(post("/v1/supply-order/bundle")
            .withRequestBody(ContainsPattern("\"bundle_ids\":[\"019dc398-fde2-7a39-bfd8-97b641fd1644\",\"019dc398-fde1-7668-b86c-230041f34734\"]"))
            .willReturn(ok(supplyBundleMulti)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)))
        stubFor(post("/v1/supply-order/bundle")
            .withRequestBody(ContainsPattern("\"bundle_ids\":[\"019d2de3-7716-7d4f-a809-4c08360d5dee\"]"))
            .willReturn(ok(supplyBundleSignle)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)))

        // 1 добавляем доставку
        chinaOrdersService.addDelivery(1L, 5000.0, 50.0, 1.0)

        // 2 добавляем ФФ
        fulfilmentAdditionalService.updateCostPrice(1L, 5000.0)

        // 3 Линкуем заказ с поставками FBO
        crossDocService.linkWithOrders(1L)

        // 4 маркируем транзакции
        costPriceScheduledService.updateTransaction()

        // 5 проверки
        transactionTemplate.execute {

            // проверяем данные по ФФ + cross doc + cost price
            val chinaOrder = chinaOrdersRepository.findById(1L).orElseThrow { AssertionError("Заказ не найден") }
            chinaOrder.costPriceEntities
                .forEach {
                    assertEquals(144.93, it.costPrice)
                    assertEquals(36.23, it.fulfilment)
                    assertEquals(7.87, it.crossDoc)
                }

            // проверяем связь заказа с поставками FBO
            var supply = ozonSupplyRepository.findByOrderId(2000046901421L)
            assertEquals(1L, supply[0].chinaOrderEntity.id)
            supply = ozonSupplyRepository.findByOrderId(2000050150604L)
            assertEquals(1L, supply[0].chinaOrderEntity.id)
            supply = ozonSupplyRepository.findByOrderId(2000050150600L)
            assertEquals(1L, supply[0].chinaOrderEntity.id)
            assertEquals(3, chinaOrder.ozonSupplyOrderIds.size)
        }
    }
}