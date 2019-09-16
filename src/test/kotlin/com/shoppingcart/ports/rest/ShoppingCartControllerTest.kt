package com.shoppingcart.ports.rest

import com.shoppingcart.application.Command.*
import com.shoppingcart.com.shoppingcart.application.CommandBus
import com.shoppingcart.com.shoppingcart.ports.rest.ShoppingCartController
import com.shoppingcart.domain.Valid
import com.shoppingcart.domain.aggregate.Price
import com.shoppingcart.ports.model.Request.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.Mockito.times
import java.util.*
import kotlin.test.assertTrue


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ShoppingCartControllerTest {

    var commandBus: CommandBus = mockk()
    var controller = ShoppingCartController(commandBus)
    val productId = UUID.randomUUID()
    val cartId = UUID.randomUUID()
    val price = Price(10)


    @Test
    fun `should convert add product to cart request to command and send it via command bus`() {

        val request = AddProductToCartRequest(productId, price.price, cartId)
        every { commandBus.send(getCommand(request)) }.answers { nothing }
        val responseEntity = controller.addProductToCart(request)
        verify { commandBus.send(getCommand(request));times(1) }
        assertTrue { responseEntity.body is Valid }
    }

    @Test
    fun `should convert remove product from cart request to command and send it via command bus`() {

        val request = RemoveProductFromCartRequest(productId, cartId)
        every { commandBus.send(getCommand(request)) }.answers { nothing }
        val responseEntity = controller.removeProductFromCart(request)
        verify { commandBus.send(getCommand(request));times(1) }
        assertTrue { responseEntity.body is Valid }
    }


    @Test
    fun `should convert calculate total price request to command and send it via command bus`() {

        val request = CalculateTotalPriceRequest(cartId)
        every { commandBus.send(getCommand(request)) }.answers { nothing }
        val responseEntity = controller.calculateTotalPrice(request)
        verify { commandBus.send(getCommand(request));times(1) }
        assertTrue { responseEntity.body is Valid }
    }


    @Test
    fun `should convert change amount of product request to command and send it via command bus`() {

        val request = ChangeAmountOfProductRequest(cartId, productId, 2)
        every { commandBus.send(getCommand(request)) }.answers { nothing }
        val responseEntity = controller.changeAmoutOfProduct(request)
        verify { commandBus.send(getCommand(request));times(1) }
        assertTrue { responseEntity.body is Valid }
    }


    private fun getCommand(request: AddProductToCartRequest): AddProductToCartCommand {
        return AddProductToCartCommand(productId, price.price, cartId)

    }

    private fun getCommand(request: RemoveProductFromCartRequest): RemoveProductFromCartCommand {
        return RemoveProductFromCartCommand(productId, cartId)

    }

    private fun getCommand(request: CalculateTotalPriceRequest): CalculateTotalPriceCommand {
        return CalculateTotalPriceCommand(cartId)

    }

    private fun getCommand(request: ChangeAmountOfProductRequest): ChangeAmountOfProductCommand {
        return ChangeAmountOfProductCommand(cartId, productId, request.amount)
    }
}