package ru.home.project.ozonapi.service.impl

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.openapitools.client.models.GetCampaignsResponse
import ru.home.project.ozonapi.client.YandexMarketClient
import ru.home.project.ozonapi.repository.PositionRepository
import ru.home.project.ozonapi.util.readResourceMoshi
import java.time.LocalDate
import java.util.concurrent.CompletableFuture

/**
 * @author rlagay
 */
class YandexReportServiceImplTest {

    private val yandexMarketClient = mock<YandexMarketClient>()
    private val positionRepository = mock<PositionRepository>()
    private val yandexService = YandexServiceImpl(yandexMarketClient, positionRepository)

    @Test
    fun `test get yandex orders`() {
        val from = LocalDate.now().minusDays(30)
        val to = LocalDate.now()

        val campaigns = readResourceMoshi("yandex/campaigns/campaigns.json", GetCampaignsResponse::class.java)

        `when`(yandexMarketClient.createReport(any(), any(), any(), any()))
            .thenReturn(CompletableFuture.completedFuture("/Users/rlagay/Documents/Github/new/ozon-api/ozon-api/src/test/resources/yandex/reports/united-marketplace-services-a04385b3-74a0-4e8e-8b88-32dfed228159.zip"))
        `when`(yandexMarketClient.getCampaignList()).thenReturn(campaigns.campaigns)

        val result = yandexService.getReport(from, to).first

        assertEquals(1575.59, result.shelf)
        assertEquals(640.0, result.crossDoc)
        assertEquals(193.63, result.paidStorage)
        assertEquals(195.0, result.utilization)
    }
}