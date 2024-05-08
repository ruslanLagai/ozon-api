package ru.home.project.ozonapi.telegram.text

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.springframework.context.ApplicationEventPublisher
import org.springframework.test.util.ReflectionTestUtils
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import ru.home.project.ozonapi.entity.ActionType
import ru.home.project.ozonapi.entity.PositionEntity
import ru.home.project.ozonapi.entity.TelegramChatEntity
import ru.home.project.ozonapi.repository.PositionRepository
import ru.home.project.ozonapi.repository.TelegramChatRepository
import ru.home.project.ozonapi.service.RevenueCalculationService
import ru.home.project.ozonapi.service.TotalRefundsService
import ru.home.project.ozonapi.service.TotalRevenueCalculationService
import ru.home.project.ozonapi.telegram.commands.*
import ru.home.project.ozonapi.util.*

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

    private val calculationsCmdProcessor = CalculationsCmdProcessor(positionRepository)
    private val addPositionCmdProcessor = AddPositionCmdProcessor(telegramChatRepository)
    private val positionsCmdProcessor = PositionsCmdProcessor(positionRepository)
    private val editPoCommandProcessor = EditPositionCmdProcessor(telegramChatRepository)
    private val positionAddedCmdProcessor = PositionAddedCmdProcessor(telegramChatRepository, positionRepository, publisher)
    private val positionEditedCmdProcessor = PositionEditedCmdProcessor(telegramChatRepository, positionRepository)
    private val refunCmdProcessor = RefundsCmdProcessor(positionRepository)
    private val commandProcessor = CommandProcessor(calculationsCmdProcessor, addPositionCmdProcessor, positionsCmdProcessor, editPoCommandProcessor, refunCmdProcessor)
    private val dateInputProcessor = DateInputProcessor(telegramChatRepository, revenueCalculationServices, totalRevenueCalculationService, totalRefundsService)
    private val positionsInputProcessor = PositionsInputProcessor(positionRepository, telegramChatRepository)
    private val inputProcessors = listOf(commandProcessor, dateInputProcessor,
        positionAddedCmdProcessor, positionsInputProcessor, positionEditedCmdProcessor)

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
            verify(telegramChatRepository).save(TelegramChatEntity(chatId = 1, positionName = "Зонт", action = ActionType.Revenue))
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
            assertEquals("Выберите товар для расчета", result?.text ?: "")
            verify(positionRepository).findAll()
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
    }

    @Nested
    inner class TestDateInput {

        @ParameterizedTest
        @ValueSource(strings = [lastDayDate, lastTwoDaysDate, forCurrentMonth, forCurrentWeek])
        fun `test predefined date - all items`(period: String) {
            val telegramChatEntity = TelegramChatEntity(id = 1, chatId = 2, positionName = allItems,
                state = true, action =  ActionType.Revenue)

            Mockito.`when`(message.chatId).thenReturn(1)
            Mockito.`when`(message.text).thenReturn(period)
            Mockito.`when`(message.isCommand).thenReturn(true)
            Mockito.`when`(telegramChatRepository.getByChatIdAndStateAndAction(1, true, ActionType.Revenue))
                .thenReturn(telegramChatEntity)

            val result = inputProcessors.map { it.processInput(period, getUpdate()) }
                .firstOrNull { it != null }
            verify(totalRevenueCalculationService).calculateRevenue(any())
            assertEquals("Не удалось рассчитать маржинальность", result?.text ?: "")
        }

        @ParameterizedTest
        @ValueSource(strings = ["1.11.2023-2.11.2023", "01.11.2023-02.11.2023", "1.11.23-2.11.23",
            "01.11.23-02.11.23"])
        fun `test date format`(input: String) {
            val telegramChatEntity = TelegramChatEntity(id = 1, chatId = 2, positionName = "зонт",
                state = true, action =  ActionType.Revenue)
            Mockito.`when`(message.chatId).thenReturn(1)
            Mockito.`when`(message.text).thenReturn(input)
            Mockito.`when`(message.isCommand).thenReturn(true)
            Mockito.`when`(telegramChatRepository.getByChatIdAndStateAndAction(1, true, ActionType.Revenue))
                .thenReturn(telegramChatEntity)

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

    private fun getUpdate(): Update {
        val update = Update()
        update.message = message
        return update
    }
}