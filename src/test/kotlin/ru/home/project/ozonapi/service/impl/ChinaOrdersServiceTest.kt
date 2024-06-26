package ru.home.project.ozonapi.service.impl

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.mock
import ru.home.project.ozonapi.entity.ChinaOrderEntity
import ru.home.project.ozonapi.entity.ChinaStockEntity
import ru.home.project.ozonapi.entity.PositionEntity
import ru.home.project.ozonapi.entity.StockEntity
import ru.home.project.ozonapi.repository.ChinaOrdersRepository
import ru.home.project.ozonapi.repository.PositionRepository
import ru.home.project.ozonapi.repository.StockRepository
import java.time.LocalDate
import java.util.*

/**
 * @author rlagay
 */
@ExtendWith(MockitoExtension::class)
class ChinaOrdersServiceTest {

    private val chinaOrdersRepository = mock<ChinaOrdersRepository>()
    private val positionRepository = mock<PositionRepository>()
    private val stockRepository = mock<StockRepository>()
    private val chinaOrdersService = ChinaOrdersService(chinaOrdersRepository, positionRepository, stockRepository)

    @Test
    fun `test add delivery `() {
        val products = listOf(
            ChinaStockEntity(name = "Зонт 1", quantity = 50, artikul = "000001", ozonId = "1"),
            ChinaStockEntity(name = "Зонт 2", quantity = 40, artikul = "000002", ozonId = "2")
        )
        val orderEntity = ChinaOrderEntity(supplier = "test", products = products)
        val saved = ChinaOrderEntity(supplier = "test", mass = 20.0, volume = 0.0, delivered = true, deliveryDate = LocalDate.now(), deliveryCost = 100.0,
            products = products)
        `when`(chinaOrdersRepository.findById(any())).thenReturn(Optional.of(orderEntity))
        `when`(positionRepository.getPositionEntityByArtikul("000001")).thenReturn(
            PositionEntity(name = "Зонт 1", artikul = "000001", ozonId = "1", additionalCost = 0.0, costPrice = 1.0))
        `when`(positionRepository.getPositionEntityByArtikul("000002")).thenReturn(
            PositionEntity(name = "Зонт 2", artikul = "000002", ozonId = "2", additionalCost = 0.0, costPrice = 1.0))
        `when`(stockRepository.getByOzonId("1")).thenReturn(StockEntity(name = "Зонт 1", artikul = "000001", quantity = 20, ozonId = "1"))

        chinaOrdersService.addDelivery(1, 100.0, 20.0, 0.0)
        verify(chinaOrdersRepository).save(saved)
        verify(stockRepository).save(StockEntity(name = "Зонт 2", artikul = "000002", quantity = 40, ozonId = "2"))
        verify(stockRepository).updateQuantityByOzonId("1", 20 + 50)
    }
}