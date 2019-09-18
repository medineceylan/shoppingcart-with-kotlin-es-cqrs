package com.shoppingcart.application

import com.shoppingcart.application.Command.*
import com.shoppingcart.com.shoppingcart.domain.events.Event.*
import com.shoppingcart.com.shoppingcart.domain.events.EventBus
import com.shoppingcart.com.shoppingcart.domain.events.EventStore
import com.shoppingcart.domain.aggregate.Price
import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.TestInstance
import org.mockito.Mockito.times
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class ShoppingCartCommandHandlerTest {

    val productId = UUID.randomUUID()
    val cartId = UUID.randomUUID()
    val price = Price(10)
    private val eventStore: EventStore = mockk()
    private val eventBus: EventBus = mockk()
    private val shoppingCartCommandHandler = ShoppingCartCommandHandler(eventBus, eventStore)


    @Before
    fun startUp() {
        every { eventStore.loadHistory(cartId) } returns emptyList()
    }

    @Test
    fun `should not raise any event when add product to cart command is invalid`() {

        val addProductToCartCommand = AddProductToCartCommand(productId, -10, cartId)
        every { eventStore.loadHistory(cartId) } returns emptyList()
        shoppingCartCommandHandler.handle(addProductToCartCommand)

        assertTrue { shoppingCartCommandHandler.getOccurredEvents().isEmpty() }
        verify { eventBus wasNot Called }

    }

    @Test
    fun `should add product to cart when command is valid`() {

        val events = mutableListOf(aProductAddedToCartEvent())
        every { eventBus.sendAll(events) }.answers { nothing }
        every { eventStore.saveAll(events) }.answers { nothing }

        shoppingCartCommandHandler.handle(anAddProductToCartCommand(10))

        verify { eventBus.sendAll(events);times(1) }
        verify { eventStore.saveAll(events);times(1) }
    }

    @Test
    fun `should remove and raise product removed from cart event  when command is valid`() {

        val events = mutableListOf(aProductRemovedFromCartEvent())
        every { eventStore.loadHistory(cartId) } returns mutableListOf(aProductAddedToCartEvent())
        every { eventBus.sendAll(events) }.answers { nothing }
        every { eventStore.saveAll(events) }.answers { nothing }

        shoppingCartCommandHandler.handle(aRemoveProductFromCartCommand())

        verify { eventBus.sendAll(events);times(1) }
        verify { eventStore.saveAll(events);times(1) }
    }

    @Test
    fun `should raise product not found in cart event  when command is valid but event couldn't applied`() {

        val events = mutableListOf(aProductNotFoundInCartEvent())

        every { eventBus.sendAll(events) }.answers { nothing }
        every { eventStore.saveAll(events) }.answers { nothing }

        shoppingCartCommandHandler.handle(aRemoveProductFromCartCommand())

        verify { eventBus.sendAll(events);times(1) }
        verify { eventStore.saveAll(events);times(1) }

    }

    @Test
    fun `should not raise any event when change amount of product command is invalid`() {

        shoppingCartCommandHandler.handle(aChangeAmountOfProductCommand(-1))
        verify { eventBus wasNot Called }
    }

    @Test
    fun `should change amount of product if command is valid`() {


        val events = mutableListOf(anAmountOfProductChangedEvent(4))

        every { eventStore.loadHistory(cartId) } returns mutableListOf(aProductAddedToCartEvent())
        every { eventBus.sendAll(events) }.answers { nothing }
        every { eventStore.saveAll(events) }.answers { nothing }

        shoppingCartCommandHandler.handle(aChangeAmountOfProductCommand(4))

        verify { eventBus.sendAll(events);times(1) }
        verify { eventStore.saveAll(events);times(1) }


    }

    @Test
    fun `should raise product not in cart event when event couldn't applied and command is valid`() {

        val events = mutableListOf(aProductNotFoundInCartEvent())

        every { eventBus.sendAll(events) }.answers { nothing }
        every { eventStore.saveAll(events) }.answers { nothing }

        shoppingCartCommandHandler.handle(aChangeAmountOfProductCommand(4))

        verify { eventBus.sendAll(events);times(1) }
        verify { eventStore.saveAll(events);times(1) }

    }


    @Test
    fun `should calculate total price command`() {

        val events = mutableListOf(aTotalPriceCalculatedEvent())

        every { eventBus.sendAll(events) }.answers { nothing }
        every { eventStore.saveAll(events) }.answers { nothing }

        shoppingCartCommandHandler.handle(aCalculateTotalPriceCommand())

        verify { eventBus.sendAll(events);times(1) }
        verify { eventStore.saveAll(events);times(1) }

    }


    private fun aProductNotFoundInCartEvent(): ProductNotFoundInCartEvent {
        return ProductNotFoundInCartEvent(productId, cartId)
    }

    private fun aRemoveProductFromCartCommand(): RemoveProductFromCartCommand {
        return RemoveProductFromCartCommand(productId = productId, cartId = cartId)
    }

    private fun aProductAddedToCartEvent(): ProductAddedToCartEvent {
        return ProductAddedToCartEvent(productId, price.price, cartId)
    }

    private fun anAmountOfProductChangedEvent(amount: Int): AmountOfProductChangedEvent {
        return AmountOfProductChangedEvent(cartId, productId, amount = amount)
    }

    private fun anAddProductToCartCommand(price: Int): AddProductToCartCommand {
        return AddProductToCartCommand(productId = productId, price = price, cartId = cartId)
    }

    private fun aChangeAmountOfProductCommand(amount: Int): ChangeAmountOfProductCommand {
        return ChangeAmountOfProductCommand(cartId = cartId, productId = productId, amount = amount)
    }

    private fun aProductRemovedFromCartEvent(): ProductRemovedFromCartEvent {
        return ProductRemovedFromCartEvent(cartId = cartId, productId = productId)
    }

    private fun aCalculateTotalPriceCommand(): CalculateTotalPriceCommand {
        return CalculateTotalPriceCommand(cartId = cartId)
    }

    private fun aTotalPriceCalculatedEvent(): TotalPriceCalculatedEvent {
        return TotalPriceCalculatedEvent(cartId = cartId)
    }

}

