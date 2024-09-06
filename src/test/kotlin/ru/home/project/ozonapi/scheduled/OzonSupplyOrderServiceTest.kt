package ru.home.project.ozonapi.scheduled

import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.openapitools.client.models.CampaignDTO
import org.openapitools.client.models.GetOrdersResponse
import org.openapitools.client.models.PlacementType
import ru.home.project.ozonapi.client.YandexMarketClient
import ru.home.project.ozonapi.entity.FbsOrderEntity
import ru.home.project.ozonapi.entity.StockEntity
import ru.home.project.ozonapi.repository.FbsOrderRepository
import ru.home.project.ozonapi.repository.OzonSupplyRepository
import ru.home.project.ozonapi.repository.PositionRepository
import ru.home.project.ozonapi.repository.StockRepository
import ru.home.project.ozonapi.service.OzonService
import ru.home.project.ozonapi.service.impl.YandexServiceImpl
import ru.home.project.ozonapi.util.readResourceMoshi
import ru.home.project.ozonapi.util.yandexInDeliveryStatuses
import java.time.LocalDate

/**
 * @author rlagay
 */
class OzonSupplyOrderServiceTest {

    private val ozonService = mock<OzonService>()
    private val yandexMarketClient = mock<YandexMarketClient>()
    private val positionRepository = mock<PositionRepository>()
    private val supplyRepository = mock< OzonSupplyRepository>()
    private val stockRepository = mock<StockRepository>()
    private val fbsRepository = mock<FbsOrderRepository>()
    private val yandexService = YandexServiceImpl(yandexMarketClient, positionRepository)
    private val supplyOrderService = OzonSupplyOrderService(ozonService, yandexService, fbsRepository, supplyRepository, stockRepository)

    @Test
    fun `test yandex stocks`() {
        val fbsOrderEntity = FbsOrderEntity(number = "1", date = LocalDate.now().minusDays(1))
        val yandexInDeliveryFbs = readResourceMoshi("yandex/orders/orders-fbs.json", GetOrdersResponse::class.java)

        `when`(fbsRepository.findFirstByOrderByDateDesc()).thenReturn(fbsOrderEntity)
        `when`(fbsRepository.getByNumberAndSubtracted("485837571", true))
            .thenReturn(FbsOrderEntity(number = "485837571", subtracted =  true, date = LocalDate.now()))
        `when`(fbsRepository.getByNumberAndSubtracted("484103298", true))
            .thenReturn(FbsOrderEntity(number = "484103298", subtracted =  true, date = LocalDate.now()))
        `when`(stockRepository.getByYandexArtikul("0000005"))
            .thenReturn(StockEntity(artikul = "", name = "Держатель для губок", ozonId = "", yandexArtikul = "", quantity = 100))
        `when`(stockRepository.getByYandexArtikul("000011")).thenReturn(StockEntity(name = "Швабра", ozonId = "", artikul = "",
            yandexArtikul = "000011", quantity = 5))

        `when`(yandexMarketClient.getCampaignList()).thenReturn(listOf(
            CampaignDTO(id = 11L, placementType = PlacementType.FBY),
            CampaignDTO(id = 22L, placementType = PlacementType.FBS)
        ))
        `when`(yandexMarketClient.getOrdersWithoutStats(22L, yandexInDeliveryStatuses, LocalDate.now().minusDays(1), LocalDate.now()))
            .thenReturn(yandexInDeliveryFbs.orders)

        `when`(yandexMarketClient.createStocksReport(11L, LocalDate.now().minusDays(1), LocalDate.now()))
            .thenReturn("/Users/rlagay/Documents/Github/new/ozon-api/ozon-api/src/test/resources/yandex/reports/sku-movements-7dab8f03-b7f0-4461-bab2-876f3ebbaa0e.zip")


        supplyOrderService.processYandexStocks()

        verify(stockRepository).updateQuantityByYandexArtikul("0000005", 95)
        verify(stockRepository).updateQuantityByYandexArtikul("0000005", 2)
        verify(fbsRepository).saveAll(anyCollection())
    }
}