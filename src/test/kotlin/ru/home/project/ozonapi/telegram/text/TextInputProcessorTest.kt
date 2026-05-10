package ru.home.project.ozonapi.telegram.text

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.springframework.context.ApplicationEventPublisher
import org.springframework.test.util.ReflectionTestUtils
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import ru.home.project.ozonapi.dto.request.ProductRequest
import ru.home.project.ozonapi.dto.response.StocksResponse
import ru.home.project.ozonapi.entity.*
import ru.home.project.ozonapi.repository.ChinaOrdersRepository
import ru.home.project.ozonapi.repository.PositionRepository
import ru.home.project.ozonapi.repository.TelegramChatRepository
import ru.home.project.ozonapi.service.*
import ru.home.project.ozonapi.telegram.commands.*
import ru.home.project.ozonapi.util.*
import java.time.LocalDate

/**
 * @author rlagay
 */
class TextInputProcessorTest {

    private val telegramChatRepository = mock<TelegramChatRepository>()
    private val positionRepository = mock<PositionRepository>()
    private val positionRevenueCalculationService = mock<RevenueCalculationService>()
    private val revenueCalculationServices = listOf(positionRevenueCalculationService)
    private val totalRevenueCalculationService = mock<TotalRevenueCalculationService>()
    private val totalRefundsService = mock<TotalRefundsService>()
    private val publisher = mock<ApplicationEventPublisher>()
    private val chinaOrdersRepository = mock<ChinaOrdersRepository>()
    private val stockService = mock<StocksService>()
    private val ordersService = mock<OrdersService>()
    private val additionalServicesForCostPriceService = mock<AdditionalServicesForCostPriceService>()
    private val crossDocService = mock<CrossDocService>()

    private val calculationsCmdProcessor = CalculationsCmdProcessor()
    private val addPositionCmdProcessor = AddPositionCmdProcessor(telegramChatRepository)
    private val positionsCmdProcessor = PositionsCmdProcessor(positionRepository)
    private val editPoCommandProcessor = EditPositionCmdProcessor(telegramChatRepository)
    private val positionAddedCmdProcessor = PositionAddedCmdProcessor(telegramChatRepository, positionRepository, publisher)
    private val positionEditedCmdProcessor = PositionEditedCmdProcessor(telegramChatRepository, positionRepository)
    private val refunCmdProcessor = RefundsCmdProcessor(positionRepository)
    private val stockWorthCmdProcessor = StockWorthCmdProcessor(stockService)
    private val deliveryDataCmdProcessor = DeliveryDataCmdProcessor(chinaOrdersRepository, telegramChatRepository)
    private val orderCmdProcessor = OrderCmdProcessor(telegramChatRepository)
    private val deliveriesCmdProcessor = DeliveriesCmdProcessor(chinaOrdersRepository)
    private val addFulfilmentCmdProcessor = AddFulfilmentCmdProcessor(chinaOrdersRepository, telegramChatRepository)

    private val commandProcessor = CommandProcessor(calculationsCmdProcessor, addPositionCmdProcessor, positionsCmdProcessor,
        editPoCommandProcessor, refunCmdProcessor, stockWorthCmdProcessor, orderCmdProcessor,
        deliveryDataCmdProcessor, deliveriesCmdProcessor, addFulfilmentCmdProcessor)
    private val dateInputProcessor = DateInputProcessor(telegramChatRepository, revenueCalculationServices,
        totalRevenueCalculationService, totalRefundsService)
    private val positionsInputProcessor = PositionsInputProcessor(positionRepository, telegramChatRepository)
    private val addOrderInputProcessor = AddOrderInputProcessor(telegramChatRepository, ordersService)
    private val addDeliveryInputProcessor = AddDeliveryInputProcessor(telegramChatRepository, ordersService)
    private val deliveryItemProcessor = DeliveryItemProcessor(telegramChatRepository, chinaOrdersRepository)
    private val fulfilmentItemProcessor = FulfilmentItemProcessor(telegramChatRepository, chinaOrdersRepository)
    private val fulfilmentDataInputProcessor = FulfilmentDataInputProcessor(
        telegramChatRepository,
        positionRepository,
        additionalServicesForCostPriceService,
        crossDocService
    )
    private val inputProcessors = listOf(commandProcessor, dateInputProcessor,
        positionAddedCmdProcessor, positionsInputProcessor, positionEditedCmdProcessor, addOrderInputProcessor,
        addDeliveryInputProcessor, deliveryItemProcessor, fulfilmentItemProcessor, fulfilmentDataInputProcessor)

    private val message = mock<Message>()

    @Nested
    inner class TestAddPosition {

        private val telegramChatEntity = TelegramChatEntity(id = 1, chatId = 2, positionName = "name",
            state = true, action =  ActionType.AddPosition)

        @Test
        fun `test add position`() {
            Mockito.`when`(message.chatId).thenReturn(1)
            Mockito.`when`(message.text).thenReturn("Держатель,10,10,123,1234")
            Mockito.`when`(message.isCommand).thenReturn(false)
            Mockito.`when`(telegramChatRepository.getByChatIdAndStateAndAction(1, true, ActionType.AddPosition))
                .thenReturn(telegramChatEntity)

            val result = inputProcessors.map { it.processInput("Держатель,10,10,123,1234", getUpdate()) }
                .first { it != null }

            assertEquals("Товар успешно добавлен", result?.text ?: "")
        }

        @Test
        fun `test add position with spaces`() {
            Mockito.`when`(message.chatId).thenReturn(1)
            Mockito.`when`(message.text).thenReturn("Держатель, 10.1, 10.2, 123, 1234")
            Mockito.`when`(message.isCommand).thenReturn(false)
            Mockito.`when`(telegramChatRepository.getByChatIdAndStateAndAction(1, true, ActionType.AddPosition))
                .thenReturn(telegramChatEntity)

            val result = inputProcessors.map { it.processInput("Держатель, 10.1, 10.2, 123, 1234", getUpdate()) }
                .first { it != null }

            assertEquals("Товар успешно добавлен", result?.text ?: "")
            verify(positionRepository).save(PositionEntity(name = "Держатель", costPrice = 10.1, additionalCost = 10.2, artikul = "123", ozonId = "1234"))
        }

        @Test
        fun `test invalid position input`() {
            Mockito.`when`(message.chatId).thenReturn(1)
            Mockito.`when`(message.text).thenReturn("Держатель,10,10,123")
            Mockito.`when`(message.isCommand).thenReturn(false)
            Mockito.`when`(telegramChatRepository.getByChatIdAndStateAndAction(1, true, ActionType.AddPosition))
                .thenReturn(telegramChatEntity)

            val result = inputProcessors.map { it.processInput("Держатель,10,10,123", getUpdate()) }
                .first { it != null }

            assertEquals("Некорректный формат ввода данные, используйте: <название>,<себестоимость>,<доп расходы>,<артикул>,<ozonId>",
                result?.text ?: "")
            verify(telegramChatRepository).updateStateByChatIdAndAction(1, false, ActionType.AddPosition)
        }

        @Test
        fun `test invalid position input - numberFormatExceptions`() {
            Mockito.`when`(message.chatId).thenReturn(1)
            Mockito.`when`(message.text).thenReturn("Держатель,10,10,123")
            Mockito.`when`(message.isCommand).thenReturn(false)
            Mockito.`when`(telegramChatRepository.getByChatIdAndStateAndAction(1, true, ActionType.AddPosition))
                .thenReturn(telegramChatEntity)

            val result = inputProcessors.map { it.processInput("Держатель,10,dsf,123,12", getUpdate()) }
                .first { it != null }

            assertEquals("Некорректный формат данных, не удалось обработать 'себестоимость' и/или 'доп расходы'", result?.text ?: "")
            verify(telegramChatRepository).updateStateByChatIdAndAction(1, false, ActionType.AddPosition)
        }
    }

    @Nested
    inner class TestPositions {

        @Test
        fun `test position name input`() {
            ReflectionTestUtils.setField(positionsInputProcessor, "positionNames", setOf("Зонт"))

            Mockito.`when`(message.chatId).thenReturn(1)
            Mockito.`when`(message.text).thenReturn("Зонт ")
            Mockito.`when`(message.isCommand).thenReturn(false)

            val result = inputProcessors.map { it.processInput("Зонт ", getUpdate()) }
                .first { it != null }

            assertEquals("Введите период, за который хотите посчитать маржинальность. Формат: 21.10.2023-21.11.2023.",
                result?.text ?: "")
            verify(telegramChatRepository).updatePositionByChatIdAnAndAction(1L, true, "Зонт")
        }

        @Test
        fun `test invalid position name input`() {
            ReflectionTestUtils.setField(positionsInputProcessor, "positionNames", setOf("Зонт"))

            Mockito.`when`(message.chatId).thenReturn(1)
            Mockito.`when`(message.text).thenReturn("Зонт 1")
            Mockito.`when`(message.isCommand).thenReturn(false)

            val result = inputProcessors.map { it.processInput("Зонт 1", getUpdate()) }
                .firstOrNull { it != null }

            assertNull(result)
        }
    }

    @Nested
    inner class TestCommandInput {

        @Test
        fun `test positions command`() {
            Mockito.`when`(message.chatId).thenReturn(1)
            Mockito.`when`(message.text).thenReturn("/positions")
            Mockito.`when`(message.isCommand).thenReturn(true)

            val result = inputProcessors.map { it.processInput("/positions", getUpdate()) }
                .firstOrNull { it != null }
            assertEquals("Доступные товары", result?.text ?: "")
        }

        @Test
        fun `test add position command`() {
            Mockito.`when`(message.chatId).thenReturn(1)
            Mockito.`when`(message.text).thenReturn("/add_position")
            Mockito.`when`(message.isCommand).thenReturn(true)

            val result = inputProcessors.map { it.processInput("/add_position", getUpdate()) }
                .firstOrNull { it != null }
            assertEquals("Добавьте товар в формате <название>,<себестоимость>,<доп расходы>,<артикул>,<ozonId>", result?.text ?: "")
            verify(telegramChatRepository).save(TelegramChatEntity(chatId = 1, positionName = "", action = ActionType.AddPosition))
        }

        @Test
        fun `test calculations command`() {
            Mockito.`when`(message.chatId).thenReturn(1)
            Mockito.`when`(message.text).thenReturn("/calculations")
            Mockito.`when`(message.isCommand).thenReturn(true)

            val result = inputProcessors.map { it.processInput("/calculations", getUpdate()) }
                .firstOrNull { it != null }
            assertEquals("Выберите магазин", result?.text ?: "")
        }

        @Test
        fun `test edit position command`() {
            Mockito.`when`(message.chatId).thenReturn(1)
            Mockito.`when`(message.text).thenReturn("/edit_position")
            Mockito.`when`(message.isCommand).thenReturn(true)

            val result = inputProcessors.map { it.processInput("/edit_position", getUpdate()) }
                .firstOrNull { it != null }
            assertEquals("Чтобы изменить данные по себестоимости товара, введите данные в формате <артикул>,<себестоимость>,<доп расходы>\n" +
                    "Если требуется изменить одно поле, оставьте поле пустым", result?.text ?: "")
            verify(telegramChatRepository).save(TelegramChatEntity(chatId = 1, positionName = "", action = ActionType.EditPosistion))
        }

        @Test
        fun `test add fulfilment command`() {
            Mockito.`when`(message.chatId).thenReturn(1)
            Mockito.`when`(message.text).thenReturn("/add_fulfilment")
            Mockito.`when`(message.isCommand).thenReturn(true)
            Mockito.`when`(chinaOrdersRepository.getChinaOrderEntityByDelivered(true))
                .thenReturn(
                    setOf(
                        ChinaOrderEntity(
                            id = 1,
                            supplier = "gomarkt",
                            number = "1234",
                            stockCost = 100.0,
                            orderDate = LocalDate.of(2024, 7, 29),
                            delivered = true,
                            ozonSupplyOrderIds = mutableSetOf(
                                OzonSupplyOrderIdEntity(orderId = 10L, chinaOrderEntity = ChinaOrderEntity(
                                    id = 99,
                                    supplier = "inner",
                                    orderDate = LocalDate.of(2024, 7, 29)
                                ))
                            )
                        )
                    )
                )

            val result = inputProcessors.map { it.processInput("/add_fulfilment", getUpdate()) }
                .firstOrNull { it != null }

            assertTrue(result?.text?.contains("Выберите поставку") == true)
            assertTrue(result?.text?.contains("gomarkt №1234 от 2024-07-29 на сумму 100.0") == true)
            verify(telegramChatRepository).save(TelegramChatEntity(chatId = 1, positionName = "", action = ActionType.AddFulfilment))
        }

        @Test
        fun `test add fulfilment command when no delivered orders`() {
            Mockito.`when`(message.chatId).thenReturn(1)
            Mockito.`when`(message.text).thenReturn("/add_fulfilment")
            Mockito.`when`(message.isCommand).thenReturn(true)
            Mockito.`when`(chinaOrdersRepository.getChinaOrderEntityByDelivered(true)).thenReturn(emptySet())

            val result = inputProcessors.map { it.processInput("/add_fulfilment", getUpdate()) }
                .firstOrNull { it != null }

            assertEquals("Поставки отсутствуют", result?.text ?: "")
            verify(telegramChatRepository).save(TelegramChatEntity(chatId = 1, positionName = "", action = ActionType.AddFulfilment))
        }
    }

    @Nested
    inner class TestDateInput {

        @ParameterizedTest
        @ValueSource(strings = [lastDayDate, lastTwoDaysDate, forCurrentMonth, forCurrentWeek])
        fun `test predefined date - all items`(period: String) {
            val telegramChatEntity = TelegramChatEntity(id = 1, chatId = 2, positionName = allItems,
                state = true, action =  ActionType.Revenue, market = MarketType.Ozon)

            Mockito.`when`(message.chatId).thenReturn(1)
            Mockito.`when`(message.text).thenReturn(period)
            Mockito.`when`(message.isCommand).thenReturn(true)
            Mockito.`when`(telegramChatRepository.getByChatIdAndState(1, true)).thenReturn(telegramChatEntity)

            val result = inputProcessors.map { it.processInput(period, getUpdate()) }
                .firstOrNull { it != null }
            verify(totalRevenueCalculationService).calculateRevenue(any())
            assertEquals("Не удалось рассчитать маржинальность, попробуйте еще раз", result?.text ?: "")
        }

        @ParameterizedTest
        @ValueSource(strings = ["1.11.2023-2.11.2023", "01.11.2023-02.11.2023", "1.11.23-2.11.23",
            "01.11.23-02.11.23"])
        fun `test date format`(input: String) {
            val telegramChatEntity = TelegramChatEntity(id = 1, chatId = 2, positionName = "зонт",
                state = true, action =  ActionType.Revenue, market = MarketType.Ozon)
            Mockito.`when`(message.chatId).thenReturn(1)
            Mockito.`when`(message.text).thenReturn(input)
            Mockito.`when`(message.isCommand).thenReturn(true)
            Mockito.`when`(telegramChatRepository.getByChatIdAndState(1, true)).thenReturn(telegramChatEntity)

            val result = inputProcessors.map { it.processInput(input, getUpdate()) }
                .firstOrNull { it != null }
            assertEquals("Не удалось рассчитать маржинальность", result?.text ?: "")
            verify(positionRevenueCalculationService).calculateRevenue(any())

        }

    }

    @Nested
    inner class TestEditPosition {

        private val telegramChatEntity = TelegramChatEntity(id = 1, chatId = 2, positionName = "name",
            state = true, action =  ActionType.EditPosistion)

        @Test
        fun `test add position`() {
            Mockito.`when`(message.chatId).thenReturn(1)
            Mockito.`when`(message.text).thenReturn("1234,10,10")
            Mockito.`when`(message.isCommand).thenReturn(false)
            Mockito.`when`(telegramChatRepository.getByChatIdAndStateAndAction(1, true, ActionType.EditPosistion))
                .thenReturn(telegramChatEntity)

            val result = inputProcessors.map { it.processInput("1234,10,10", getUpdate()) }
                .first { it != null }

            assertEquals("Данные по себестоимости успешно изменены", result?.text ?: "")
        }

        @Test
        fun `test edit position with spaces`() {
            Mockito.`when`(message.chatId).thenReturn(1)
            Mockito.`when`(message.text).thenReturn("1234, 10.1, 10.2")
            Mockito.`when`(message.isCommand).thenReturn(false)
            Mockito.`when`(telegramChatRepository.getByChatIdAndStateAndAction(1, true, ActionType.EditPosistion))
                .thenReturn(telegramChatEntity)

            val result = inputProcessors.map { it.processInput("1234, 10.1, 10.2", getUpdate()) }
                .first { it != null }

            assertEquals("Данные по себестоимости успешно изменены", result?.text ?: "")
            verify(positionRepository).updateByArtikul(costPrice = 10.1, addCosts = 10.2, artikul = "1234")
        }

        @Test
        fun `test edit position only cost price`() {
            Mockito.`when`(message.chatId).thenReturn(1)
            Mockito.`when`(message.text).thenReturn("1234, 10.1, ")
            Mockito.`when`(message.isCommand).thenReturn(false)
            Mockito.`when`(telegramChatRepository.getByChatIdAndStateAndAction(1, true, ActionType.EditPosistion))
                .thenReturn(telegramChatEntity)

            val result = inputProcessors.map { it.processInput("1234, 10.1, ", getUpdate()) }
                .first { it != null }

            assertEquals("Данные по себестоимости успешно изменены", result?.text ?: "")
            verify(positionRepository).updateCostPriceByArtikul(costPrice = 10.1, artikul = "1234")
        }

        @Test
        fun `test edit position only add costs`() {
            Mockito.`when`(message.chatId).thenReturn(1)
            Mockito.`when`(message.text).thenReturn("1234,,10.1")
            Mockito.`when`(message.isCommand).thenReturn(false)
            Mockito.`when`(telegramChatRepository.getByChatIdAndStateAndAction(1, true, ActionType.EditPosistion))
                .thenReturn(telegramChatEntity)

            val result = inputProcessors.map { it.processInput("1234,,10.1", getUpdate()) }
                .first { it != null }

            assertEquals("Данные по себестоимости успешно изменены", result?.text ?: "")
            verify(positionRepository).updateAddCostsByArtikul(addCosts = 10.1, artikul = "1234")
        }

        @Test
        fun `test invalid position input`() {
            Mockito.`when`(message.chatId).thenReturn(1)
            Mockito.`when`(message.text).thenReturn(",10,10")
            Mockito.`when`(message.isCommand).thenReturn(false)
            Mockito.`when`(telegramChatRepository.getByChatIdAndStateAndAction(1, true, ActionType.EditPosistion))
                .thenReturn(telegramChatEntity)

            val result = inputProcessors.map { it.processInput(",10,10", getUpdate()) }
                .first { it != null }

            assertEquals("Некорректный формат ввода данных: отсутствует артикул, используйте: <артикул>,<себестоимость>,<доп расходы>",
                result?.text ?: "")
            verify(telegramChatRepository).updateStateByChatIdAndAction(1, false, ActionType.EditPosistion)
        }

        @Test
        fun `test invalid position input - numberFormatExceptions`() {
            Mockito.`when`(message.chatId).thenReturn(1)
            Mockito.`when`(message.text).thenReturn("123,10,dsf")
            Mockito.`when`(message.isCommand).thenReturn(false)
            Mockito.`when`(telegramChatRepository.getByChatIdAndStateAndAction(1, true, ActionType.EditPosistion))
                .thenReturn(telegramChatEntity)

            val result = inputProcessors.map { it.processInput("123,10,dsf", getUpdate()) }
                .first { it != null }

            assertEquals("Некорректный формат данных, не удалось обработать 'себестоимость' и/или 'доп расходы'", result?.text ?: "")
            verify(telegramChatRepository).updateStateByChatIdAndAction(1, false, ActionType.EditPosistion)
        }
    }

    @Nested
    inner class TestAddOrderInput {

        private val errorMsg = "Добавьте данные по поставке в формате: \n" +
                "<наименование поставки>,<стоимость товара>,<номер заказа (при наличии)>\n" +
                "<артикул товара>,<количество>,<цена>\n" +
                "<артикул товара>,<количество>,<цена>\n"

        private val telegramChatEntity = TelegramChatEntity(id = 1, chatId = 2, positionName = "",
            state = true, action =  ActionType.AddOrder)

        @Test
        fun `test invalid input - no positions`() {
            Mockito.`when`(message.chatId).thenReturn(1)
            Mockito.`when`(message.text).thenReturn("1234,10,10")
            Mockito.`when`(message.isCommand).thenReturn(false)
            Mockito.`when`(telegramChatRepository.getByChatIdAndStateAndAction(1, true, ActionType.AddOrder))
                .thenReturn(telegramChatEntity)

            val result = inputProcessors.map { it.processInput("1234,10,10", getUpdate()) }
                .first { it != null }

            assertEquals(errorMsg, result?.text ?: "")
        }

        @Test
        fun `test invalid input - invalid number format`() {
            Mockito.`when`(message.chatId).thenReturn(1)
            Mockito.`when`(message.text).thenReturn("1234,10,10")
            Mockito.`when`(message.isCommand).thenReturn(false)
            Mockito.`when`(telegramChatRepository.getByChatIdAndStateAndAction(1, true, ActionType.AddOrder))
                .thenReturn(telegramChatEntity)

            val result = inputProcessors.map { it.processInput(
                "gomarkt,100,asfdsg" +
                        "\n00012,45,4" +
                        "\n0021,dasf,5",
                getUpdate()) }
                .first { it != null }

            assertEquals("Некорректный формат данных, не удалось обработать 'количество' / 'цену'", result?.text ?: "")
        }

        @Test
        fun `test invalid input - positions with errors`() {
            Mockito.`when`(message.chatId).thenReturn(1)
            Mockito.`when`(message.text).thenReturn("1234,10,10")
            Mockito.`when`(message.isCommand).thenReturn(false)
            Mockito.`when`(telegramChatRepository.getByChatIdAndStateAndAction(1, true, ActionType.AddOrder))
                .thenReturn(telegramChatEntity)

            val result = inputProcessors.map { it.processInput(
                "gomarkt,100,asfdsg" +
                        "\n00012,45",
                getUpdate()) }
                .first { it != null }
            assertEquals(errorMsg, result?.text ?: "")
        }

        @Test
        fun `test add order`() {
            Mockito.`when`(message.chatId).thenReturn(1)
            Mockito.`when`(message.text).thenReturn("1234,10,10")
            Mockito.`when`(message.isCommand).thenReturn(false)
            Mockito.`when`(telegramChatRepository.getByChatIdAndStateAndAction(1, true, ActionType.AddOrder))
                .thenReturn(telegramChatEntity)

            val result = inputProcessors.map { it.processInput(
                "gomarkt,100,number" +
                        "\n00012,45,7.6" +
                        "\n00014,40,19.8",
                getUpdate()) }
                .first { it != null }

            assertEquals("Поставка успешно добавлена", result?.text ?: "")
            val products = listOf(
                ProductRequest(artikul = "00012", quantity = 45, price = 7.6),
                ProductRequest(artikul = "00014", quantity = 40, price = 19.8)
            )
            verify(ordersService).saveNewOrder("gomarkt", 100.0, "number", products = products)
        }
    }

    @Nested
    inner class TestDeliveryItemInput {

        private val msg = "Добавьте данные по доставке в формате: \n" +
                "<стоимость доставки>,<масса груза>,<объем груза (при наличии)>\n"
        private val errorMsg = "Не удалось найти поставку"

        private val telegramChatEntity = TelegramChatEntity(id = 1, chatId = 2, positionName = "",
            state = true, action =  ActionType.AddDelivery)

        @Test
        fun `test supplier + price input`() {
            Mockito.`when`(message.chatId).thenReturn(1)
            Mockito.`when`(message.text).thenReturn("1234,10,10")
            Mockito.`when`(message.isCommand).thenReturn(false)
            Mockito.`when`(telegramChatRepository.getByChatIdAndStateAndAction(1, true, ActionType.AddDelivery))
                .thenReturn(telegramChatEntity)
            Mockito.`when`(chinaOrdersRepository.getChinaOrderEntityByDelivered(false))
                .thenReturn(setOf(ChinaOrderEntity(id = 1, supplier = "gomarkt", number = "1234", stockCost = 100.0, orderDate = LocalDate.of(2024, 7, 29))))

            val result = inputProcessors.map { it.processInput("gomarkt №1234 от 2024-07-29 на сумму 100.0", getUpdate()) }
                .first { it != null }

            assertEquals(msg, result?.text ?: "")
        }

        @Test
        fun `test number input`() {
            Mockito.`when`(message.chatId).thenReturn(1)
            Mockito.`when`(message.text).thenReturn("1234,10,10")
            Mockito.`when`(message.isCommand).thenReturn(false)
            Mockito.`when`(telegramChatRepository.getByChatIdAndStateAndAction(1, true, ActionType.AddDelivery))
                .thenReturn(telegramChatEntity)
            Mockito.`when`(chinaOrdersRepository.getChinaOrderEntityByDelivered(false))
                .thenReturn(setOf(ChinaOrderEntity(id = 1, supplier = "gomarkt", number = "1234", stockCost = 100.0, orderDate = LocalDate.of(2024, 7, 16))))

            val result = inputProcessors.map { it.processInput("gomarkt №1234 от 2024-07-16 на сумму 100.0", getUpdate()) }
                .first { it != null }

            assertEquals(msg, result?.text ?: "")
        }

        @Test
        fun `test order is not found`() {
            Mockito.`when`(message.chatId).thenReturn(1)
            Mockito.`when`(message.text).thenReturn("1234,10,10")
            Mockito.`when`(message.isCommand).thenReturn(false)
            Mockito.`when`(telegramChatRepository.getByChatIdAndStateAndAction(1, true, ActionType.AddDelivery))
                .thenReturn(telegramChatEntity)
            Mockito.`when`(chinaOrdersRepository.getChinaOrderEntityByDelivered(false))
                .thenReturn(setOf(ChinaOrderEntity(supplier = "gomarkt", number = "1234", stockCost = 100.0, orderDate = LocalDate.now())))

            val result = inputProcessors.map { it.processInput("12342", getUpdate()) }
                .first { it != null }

            assertEquals(errorMsg, result?.text ?: "")
        }

    }

    @Nested
    inner class TestAddDeliveryInput {

        private val errorMsg = "Добавьте данные по поставке в формате: \n" +
                "<стоимость доставки>,<масса груза>,<вес груза>"

        private val telegramChatEntity = TelegramChatEntity(id = 1, chatId = 2, positionName = "",
            state = true, action =  ActionType.AddDelivery, deliveryId = 1)

        @Test
        fun `test add delivery data`() {
            Mockito.`when`(message.chatId).thenReturn(1)
            Mockito.`when`(message.text).thenReturn("1234,10,10")
            Mockito.`when`(message.isCommand).thenReturn(false)
            Mockito.`when`(telegramChatRepository.getByChatIdAndStateAndAction(1, true, ActionType.AddDelivery))
                .thenReturn(telegramChatEntity)
            Mockito.`when`(chinaOrdersRepository.getChinaOrderEntityByDelivered(false))
                .thenReturn(setOf(ChinaOrderEntity(id = 1, supplier = "gomarkt", number = "1234", stockCost = 100.0, orderDate = LocalDate.now())))

            val result = inputProcessors.map { it.processInput("21232,32.2,0.2", getUpdate()) }
                .first { it != null }

            assertEquals("Данные по доставке успешно добавлены", result?.text ?: "")
            verify(ordersService).addDelivery(1, 21232.0, 32.2, 0.2)
        }

        @Test
        fun `test add delivery data - no volume`() {
            Mockito.`when`(message.chatId).thenReturn(1)
            Mockito.`when`(message.text).thenReturn("1234,10,10")
            Mockito.`when`(message.isCommand).thenReturn(false)
            Mockito.`when`(telegramChatRepository.getByChatIdAndStateAndAction(1, true, ActionType.AddDelivery))
                .thenReturn(telegramChatEntity)
            Mockito.`when`(chinaOrdersRepository.getChinaOrderEntityByDelivered(false))
                .thenReturn(setOf(ChinaOrderEntity(id = 1, supplier = "gomarkt", number = "1234", stockCost = 100.0, orderDate = LocalDate.now())))

            val result = inputProcessors.map { it.processInput("21232,32.2", getUpdate()) }
                .first { it != null }

            assertEquals("Данные по доставке успешно добавлены", result?.text ?: "")
            verify(ordersService).addDelivery(1, 21232.0, 32.2, 0.0)
        }

        @Test
        fun `test number format exception`() {
            Mockito.`when`(message.chatId).thenReturn(1)
            Mockito.`when`(message.text).thenReturn("1234,10,10")
            Mockito.`when`(message.isCommand).thenReturn(false)
            Mockito.`when`(telegramChatRepository.getByChatIdAndStateAndAction(1, true, ActionType.AddDelivery))
                .thenReturn(telegramChatEntity)
            Mockito.`when`(chinaOrdersRepository.getChinaOrderEntityByDelivered(false))
                .thenReturn(setOf(ChinaOrderEntity(id = 1, supplier = "gomarkt", number = "1234", stockCost = 100.0, orderDate = LocalDate.now())))

            val result = inputProcessors.map { it.processInput("21232,32.2.2,0.2", getUpdate()) }
                .first { it != null }

            assertEquals("Некорректный формат данных, не удалось обработать 'массу' / 'сумму'", result?.text ?: "")
            verify(ordersService, times(0)).addDelivery(1, 21232.0, 32.2, 0.2)
        }

        @Test
        fun `test invalid input`() {
            Mockito.`when`(message.chatId).thenReturn(1)
            Mockito.`when`(message.text).thenReturn("1234,10,10")
            Mockito.`when`(message.isCommand).thenReturn(false)
            Mockito.`when`(telegramChatRepository.getByChatIdAndStateAndAction(1, true, ActionType.AddDelivery))
                .thenReturn(telegramChatEntity)
            Mockito.`when`(chinaOrdersRepository.getChinaOrderEntityByDelivered(false))
                .thenReturn(setOf(ChinaOrderEntity(id = 1, supplier = "gomarkt", number = "1234", stockCost = 100.0, orderDate = LocalDate.now())))

            val result = inputProcessors.map { it.processInput("21232,32.2,a,21", getUpdate()) }
                .first { it != null }

            assertEquals(errorMsg, result?.text ?: "")
            verify(ordersService, times(0)).addDelivery(1, 21232.0, 32.2, 0.0)
        }
    }

    @Nested
    inner class TestFulfilmentItemInput {

        private val telegramChatEntity = TelegramChatEntity(id = 1, chatId = 1, positionName = "",
            state = true, action = ActionType.AddFulfilment)

        @Test
        fun `test fulfilment item selection`() {
            val order = ChinaOrderEntity(
                id = 7,
                supplier = "gomarkt",
                number = "1234",
                stockCost = 100.0,
                orderDate = LocalDate.of(2024, 7, 29),
                delivered = true
            )

            Mockito.`when`(message.chatId).thenReturn(1)
            Mockito.`when`(message.text).thenReturn("gomarkt №1234 от 2024-07-29 на сумму 100.0")
            Mockito.`when`(message.isCommand).thenReturn(false)
            Mockito.`when`(telegramChatRepository.getByChatIdAndStateAndAction(1, true, ActionType.AddFulfilment))
                .thenReturn(telegramChatEntity)
            Mockito.`when`(chinaOrdersRepository.getChinaOrderEntityByDelivered(true)).thenReturn(setOf(order))

            val result = inputProcessors.map { it.processInput("gomarkt №1234 от 2024-07-29 на сумму 100.0", getUpdate()) }
                .first { it != null }

            assertEquals(
                "Добавьте данные по доставке в формате: \n<стоимость доставки>,<масса груза>,<объем груза (при наличии)>\n",
                result?.text ?: ""
            )
            assertEquals(7L, telegramChatEntity.deliveryId)
            verify(telegramChatRepository).save(telegramChatEntity)
        }

        @Test
        fun `test fulfilment item order is not found`() {
            Mockito.`when`(message.chatId).thenReturn(1)
            Mockito.`when`(message.text).thenReturn("unknown order")
            Mockito.`when`(message.isCommand).thenReturn(false)
            Mockito.`when`(telegramChatRepository.getByChatIdAndStateAndAction(1, true, ActionType.AddFulfilment))
                .thenReturn(telegramChatEntity)
            Mockito.`when`(chinaOrdersRepository.getChinaOrderEntityByDelivered(true))
                .thenReturn(setOf(ChinaOrderEntity(id = 7, supplier = "gomarkt", number = "1234", stockCost = 100.0, orderDate = LocalDate.of(2024, 7, 29), delivered = true)))

            val result = inputProcessors.map { it.processInput("unknown order", getUpdate()) }
                .first { it != null }

            assertEquals("Не удалось найти поставку", result?.text ?: "")
        }

        @Test
        fun `test fulfilment item returns null for command or csv input`() {
            Mockito.`when`(message.chatId).thenReturn(1)
            Mockito.`when`(message.text).thenReturn("1,2")
            Mockito.`when`(message.isCommand).thenReturn(false)
            Mockito.`when`(telegramChatRepository.getByChatIdAndStateAndAction(1, true, ActionType.AddFulfilment))
                .thenReturn(telegramChatEntity)

            val csvResult = fulfilmentItemProcessor.processInput("1,2", getUpdate())
            assertNull(csvResult)

            Mockito.`when`(message.isCommand).thenReturn(true)
            val commandResult = fulfilmentItemProcessor.processInput("/cmd", getUpdate())
            assertNull(commandResult)
        }
    }

    @Nested
    inner class TestFulfilmentDataInput {

        private val telegramChatEntity = TelegramChatEntity(id = 1, chatId = 1, positionName = "",
            state = true, action = ActionType.AddFulfilment, deliveryId = 77)

        @Test
        fun `test fulfilment data input`() {
            Mockito.`when`(message.chatId).thenReturn(1)
            Mockito.`when`(message.text).thenReturn("123.45")
            Mockito.`when`(message.isCommand).thenReturn(false)
            Mockito.`when`(telegramChatRepository.getByChatIdAndStateAndAction(1, true, ActionType.AddFulfilment))
                .thenReturn(telegramChatEntity)

            val result = fulfilmentDataInputProcessor.processInput("123.45", getUpdate())

            assertEquals("Услуги ФФ добавлены в себестоимость FIFO", result?.text ?: "")
            verify(additionalServicesForCostPriceService).updateCostPrice(77, 123.45)
            verify(crossDocService).linkWithOrders(77)
            verify(telegramChatRepository).updateStateByChatIdAndAction(1, false, ActionType.AddFulfilment)
        }

        @Test
        fun `test fulfilment data invalid format`() {
            Mockito.`when`(message.chatId).thenReturn(1)
            Mockito.`when`(message.text).thenReturn("1,2")
            Mockito.`when`(message.isCommand).thenReturn(false)
            Mockito.`when`(telegramChatRepository.getByChatIdAndStateAndAction(1, true, ActionType.AddFulfilment))
                .thenReturn(telegramChatEntity)

            val result = fulfilmentDataInputProcessor.processInput("1,2", getUpdate())

            assertEquals("Некорректный формат ввода данные", result?.text ?: "")
            verify(telegramChatRepository, times(1)).updateStateByChatIdAndAction(1, false, ActionType.AddFulfilment)
        }

        @Test
        fun `test fulfilment data number format exception`() {
            Mockito.`when`(message.chatId).thenReturn(1)
            Mockito.`when`(message.text).thenReturn("abc")
            Mockito.`when`(message.isCommand).thenReturn(false)
            Mockito.`when`(telegramChatRepository.getByChatIdAndStateAndAction(1, true, ActionType.AddFulfilment))
                .thenReturn(telegramChatEntity)

            val result = fulfilmentDataInputProcessor.processInput("abc", getUpdate())

            assertEquals("Некорректный формат данных, не удалось обработать 'стоимость ФФ'", result?.text ?: "")
            verify(telegramChatRepository, times(1)).updateStateByChatIdAndAction(1, false, ActionType.AddFulfilment)
        }

        @Test
        fun `test fulfilment data returns null for inactive chat`() {
            Mockito.`when`(message.chatId).thenReturn(1)
            Mockito.`when`(message.text).thenReturn("123")
            Mockito.`when`(message.isCommand).thenReturn(false)
            Mockito.`when`(telegramChatRepository.getByChatIdAndStateAndAction(1, true, ActionType.AddFulfilment))
                .thenReturn(null)

            val result = fulfilmentDataInputProcessor.processInput("123", getUpdate())

            assertNull(result)
            verify(telegramChatRepository).updateStateByChatIdAndAction(1, false, ActionType.AddFulfilment)
        }
    }


    @Nested
    inner class TestStockWorthInput {

        private val errorMsg = "Добавьте данные по поставке в формате: \n" +
                "<стоимость доставки>,<масса груза>,<вес груза>"

        private val telegramChatEntity = TelegramChatEntity(id = 1, chatId = 2, positionName = "",
            state = true, action =  ActionType.AddDelivery, deliveryId = 1)

        @Test
        fun `test add delivery data`() {
            Mockito.`when`(message.chatId).thenReturn(1)
            Mockito.`when`(message.text).thenReturn("/stock_worth")
            Mockito.`when`(message.isCommand).thenReturn(true)
            Mockito.`when`(telegramChatRepository.getByChatIdAndStateAndAction(1, true, ActionType.AddDelivery))
                .thenReturn(telegramChatEntity)
            Mockito.`when`(stockService.getStocks()).thenReturn(StocksResponse())
            val result = inputProcessors.map { it.processInput("/stock_worth", getUpdate()) }
                .first { it != null }

            assertEquals("Стоимость товара:\n" +
                    "- товар на складе            0.0\n" +
                    "\n" +
                    "- товар в доставке Озон           0.0\n" +
                    "\n" +
                    "- товар в пути на склад Озон           0.0\n" +
                    "\n" +
                    "- заказы из Китая            0.0\n" +
                    "\n", result?.text ?: "")
        }

    }

    private fun getUpdate(): Update {
        val update = Update()
        update.message = message
        return update
    }
}