package com.shoppingcart.domain.aggregate

import com.shoppingcart.application.Command.AddProductToCartCommand
import com.shoppingcart.application.Command.ChangeAmountOfProductCommand
import com.shoppingcart.com.shoppingcart.domain.events.Event
import com.shoppingcart.com.shoppingcart.domain.events.Event.*
import com.shoppingcart.domain.Invalid
import com.shoppingcart.domain.Valid
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertTrue
import java.util.*


class CartEntityTest {

    companion object {
        val cartId = UUID.randomUUID()
        val productId = UUID.randomUUID()
        val price = Price(10)
        var cart = CartEntity(cartId)

    }

    @Before
    fun startUp() {
        cart.cartItems.clear()
    }

    @Test
    fun `should add product and changed amount of product`() {

        val events = mutableListOf<Event>()
        events.add(aProductAddedToCartEvent())
        events.add(anAmountOfProductChangedEvent())
        cart.applyAll(events)

        assertTrue { cart.cartItems.size == 1 }
        assertTrue { cart.cartItems[productId]?.quantity == 4 }
    }

    @Test
    fun `should return valid command result when product is added successfully`() {

        val result = cart.handle(anAddProductToCartCommand(10))
        assertTrue(result is Valid)

    }

    @Test
    fun `should return invalid command result when command is not valid`() {

        val result = cart.handle(anAddProductToCartCommand(-20))
        assertTrue { result is Invalid }
    }

    @Test
    fun `product should be added to cart`() {

        val event = aProductAddedToCartEvent()
        cart.apply(event)

        assertTrue { cart.cartItems.size == 1 }
        assertTrue { cart.cartItems[event.productId]?.quantity == 1 }
    }

    @Test
    fun `quantity should be increased if the same product already added `() {

        cart.apply(aProductAddedToCartEvent())
        cart.apply(aProductAddedToCartEvent())

        assertTrue { cart.cartItems.size == 1 }
        assertTrue { cart.cartItems[productId]?.quantity == 2 }
    }

    @Test
    fun `should return valid if the product already added and ready for change `() {

        cart.apply(aProductAddedToCartEvent())
        val result = cart.handle(aChangeAmountOfProductCommand())
        assertTrue { result is Valid }
    }


    @Test
    fun `amount of product should be changed `() {

        cart.apply(aProductAddedToCartEvent())
        cart.apply(anAmountOfProductChangedEvent())

        assertTrue { cart.cartItems.size == 1 }
        assertTrue { cart.cartItems[productId]?.quantity == 4 }

    }


    @Test
    fun `should calculate and return total prices`() {

        cart.apply(aProductAddedToCartEvent())
        cart.apply(aProductAddedToCartEvent())
        cart.apply(aProductAddedToCartEvent())
        cart.apply(aTotalPriceCalculatedEvent())
        assertTrue { cart.cartItems.size == 1 }
        assertTrue { cart.totalPrice == 30 }

    }

    @Test
    fun `product should be removed from cart`() {
        cart.apply(aProductAddedToCartEvent())
        assertTrue { cart.cartItems.size == 1 }
        cart.apply(aProductRemovedFromCartEvent())
        assertTrue { cart.cartItems.isEmpty() }

    }


    private fun aTotalPriceCalculatedEvent(): TotalPriceCalculatedEvent {
        return TotalPriceCalculatedEvent(cartId)
    }

    private fun aProductAddedToCartEvent(): ProductAddedToCartEvent {
        return ProductAddedToCartEvent(productId, price.price, cartId)
    }

    private fun anAmountOfProductChangedEvent(): AmountOfProductChangedEvent {
        return AmountOfProductChangedEvent(cartId, productId, amount = 4)
    }

    private fun anAddProductToCartCommand(price: Int): AddProductToCartCommand {
        return AddProductToCartCommand(productId = productId, price = price, cartId = cartId)
    }

    private fun aChangeAmountOfProductCommand(): ChangeAmountOfProductCommand {
        return ChangeAmountOfProductCommand(cartId = cartId, productId = productId, amount = 4)
    }

    private fun aProductRemovedFromCartEvent(): ProductRemovedFromCartEvent {
        return ProductRemovedFromCartEvent(cartId = cartId, productId = productId)
    }


}
