package ru.home.project.ozonapi.service.impl

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import ru.home.project.ozonapi.dto.supply.response.GetSupplyOrdersResp
import ru.home.project.ozonapi.dto.supply.response.SupplyBundleItem
import ru.home.project.ozonapi.dto.supply.response.SupplyBundlesResp
import ru.home.project.ozonapi.dto.supply.response.SupplyOrdersResp
import ru.home.project.ozonapi.entity.ChinaOrderEntity
import ru.home.project.ozonapi.entity.ChinaStockEntity
import ru.home.project.ozonapi.entity.OzonSupplyOrderIdEntity
import ru.home.project.ozonapi.exception.InvalidChineOrderException
import ru.home.project.ozonapi.exception.NoSupplyItemException
import ru.home.project.ozonapi.repository.ChinaOrdersRepository
import ru.home.project.ozonapi.service.OzonService
import ru.home.project.ozonapi.util.readResource
import java.time.LocalDate
import java.util.*

@ExtendWith(MockitoExtension::class)
class CrossDocServiceImplTest {

    private val chinaOrdersRepository = mock<ChinaOrdersRepository>()
    private val ozonService = mock<OzonService>()
    private val crossDocService = CrossDocServiceImpl(chinaOrdersRepository, ozonService)

    private val supplyOrders = readResource("cross-doc/supply-list.json", SupplyOrdersResp::class.java).supplyOrders
    private val supplyOrdersDetails = readResource("cross-doc/get-supply-orders-nes.json", GetSupplyOrdersResp::class.java).orders
    private val supplyBundle1 = readResource("cross-doc/supply-bundle-1.json", SupplyBundlesResp::class.java).items
    private val supplyBundle2 = readResource("cross-doc/supply-bundle-2.json", SupplyBundlesResp::class.java).items
    private val supplyBundle3 = readResource("cross-doc/supply-bundle-3.json", SupplyBundlesResp::class.java).items

    @Test
    fun `links order when products are split across several supplies`() {
        val expectedOrderIds = listOf(2000046901421, 2000050150604, 2000050150600)
        val expectedBundleIds = listOf("019d2de3-7716-7d4f-a809-4c08360d5dee", "019dc398-fde2-7a39-bfd8-97b641fd1644", "019dc398-fde1-7668-b86c-230041f34734")

        val order = chinaOrder(
            id = 1L,
            products = listOf(
                chinaProduct(sku = 1134715033L, quantity = 75),
                chinaProduct(sku = 1134731178L, quantity = 21),
                chinaProduct(sku = 1134733705L, quantity = 32),
                chinaProduct(sku = 3389954573L, quantity = 10)
            )
        )
        stubOrder(order)
        stubSupplyData(
            mapOf(
                supplyOrders[0] to supplyBundle1,
                supplyOrders[1] to supplyBundle2,
                supplyOrders[2] to supplyBundle3
            )
        )
        whenever(ozonService.getSupplyOrderIds(any())).thenReturn(
            supplyOrdersDetails.flatMap { it.supplies!!.map { item -> Pair(item.supplyId, item.bundleId) } }.toList())

        crossDocService.linkWithOrders(order.id!!)

        assertEquals(expectedOrderIds, order.ozonSupplyOrderIds.map { it.orderId })
        assertEquals(expectedBundleIds, order.ozonSupplyOrderIds.map { it.bundleId })
    }

    @Test
    fun `links order when all products are in one supply bundle`() {
        val expectedOrderIds = listOf(2000046901421)
        val order = chinaOrder(
            id = 2L,
            products = listOf(
                chinaProduct(sku = 1134715033L, quantity = 32),
                chinaProduct(sku = 1134731178L, quantity = 11),
                chinaProduct(sku = 1134733705L, quantity = 11)
            )
        )
        stubOrder(order)
        stubSupplyData(
            mapOf(
                supplyOrders[0] to supplyBundle1,
                supplyOrders[1] to supplyBundle2,
                supplyOrders[2] to supplyBundle3
            )
        )
        whenever(ozonService.getSupplyOrderIds(eq(setOf(supplyOrders[0]))))
            .thenReturn(listOf(Pair(2000046901421, "12")))

        crossDocService.linkWithOrders(order.id!!)

        assertEquals(expectedOrderIds, order.ozonSupplyOrderIds.map { it.orderId })
    }

    @Test
    fun `throws when there is not enough quantity in supplies`() {
        val order = chinaOrder(
            id = 3L,
            products = listOf(
                chinaProduct(sku = 1134715033L, quantity = 90),
                chinaProduct(sku = 1134731178L, quantity = 10),
                chinaProduct(sku = 1134733705L, quantity = 10)
            )
        )
        stubOrder(order)
        stubSupplyData(
            mapOf(
                supplyOrders[0] to supplyBundle1,
                supplyOrders[1] to supplyBundle2,
                supplyOrders[2] to supplyBundle3
            )
        )

        val error = assertThrows<NoSupplyItemException> {
            crossDocService.linkWithOrders(order.id!!)
        }

        assertTrue(error.message!!.contains("недостаточном количестве"))
        assertTrue(error.message!!.contains("1134715033"))
    }

    @Test
    fun `throws when not all sku are present in supplies`() {
        val order = chinaOrder(
            id = 4L,
            products = listOf(
                chinaProduct(sku = 1134715033L, quantity = 30),
                chinaProduct(sku = 9999999999L, quantity = 5)
            )
        )
        stubOrder(order)
        stubSupplyData(
            mapOf(
                supplyOrders[0] to supplyBundle1,
                supplyOrders[1] to supplyBundle2,
                supplyOrders[2] to supplyBundle3
            )
        )

        val error = assertThrows<NoSupplyItemException> {
            crossDocService.linkWithOrders(order.id!!)
        }

        assertTrue(error.message!!.contains("Продукты не найдены в поставках"))
        assertTrue(error.message!!.contains("9999999999"))
    }

    @Test
    fun `does not duplicate already linked supply ids`() {
        val expectedOrderIds = listOf(2000046901421)
        val order = chinaOrder(
            id = 5L,
            products = listOf(
                chinaProduct(sku = 1134715033L, quantity = 31),
                chinaProduct(sku = 1134731178L, quantity = 11),
                chinaProduct(sku = 1134733705L, quantity = 11)
            )
        )
        order.ozonSupplyOrderIds.add(OzonSupplyOrderIdEntity(orderId = 2000046901421, chinaOrderEntity = order, bundleId = "12"))
        stubOrder(order)
        stubSupplyData(
            mapOf(
                supplyOrders[0] to supplyBundle1,
                supplyOrders[1] to supplyBundle2,
                supplyOrders[2] to supplyBundle3
            )
        )

        crossDocService.linkWithOrders(order.id!!)

        assertEquals(1, order.ozonSupplyOrderIds.size)
        assertEquals(expectedOrderIds, order.ozonSupplyOrderIds.map { it.orderId })
        assertEquals(listOf("12"), order.ozonSupplyOrderIds.map { it.bundleId })
    }

    @Test
    fun `throws when china order is not found`() {
        whenever(chinaOrdersRepository.findByIdWithOzonSupplyIds(999L)).thenReturn(Optional.empty())

        val error = assertThrows<InvalidChineOrderException> {
            crossDocService.linkWithOrders(999L)
        }

        assertTrue(error.message!!.contains("999"))
    }


    private fun stubOrder(order: ChinaOrderEntity) {
        whenever(chinaOrdersRepository.findByIdWithOzonSupplyIds(order.id!!)).thenReturn(Optional.of(order))
        whenever(chinaOrdersRepository.save(order)).thenReturn(order)
    }

    private fun stubSupplyData(itemsByOrderId: Map<Int, List<SupplyBundleItem>>) {
        whenever(ozonService.getSupplyOrders(any())).thenReturn(supplyOrders)
        itemsByOrderId.forEach { (orderId, items) ->
            whenever(ozonService.getSupplyItemsInOrder(listOf(orderId))).thenReturn(items)
        }
    }

    private fun chinaOrder(
        id: Long,
        products: List<ChinaStockEntity>
    ): ChinaOrderEntity {
        val order = ChinaOrderEntity(
            id = id,
            supplier = "supplier",
            orderDate = LocalDate.now(),
            stockCost = 1000.0,
            products = products.toMutableList(),
            ozonSupplyOrderIds = mutableSetOf()
        )
        order.products.forEach { it.chinaOrderEntity = order }
        return order
    }

    private fun chinaProduct(sku: Long, quantity: Int): ChinaStockEntity =
        ChinaStockEntity(
            name = "product-$sku",
            quantity = quantity,
            ozonId = sku.toString(),
            artikul = "art-$sku",
            priceRub = 100.0
        )
}
