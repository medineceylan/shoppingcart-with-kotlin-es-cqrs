package com.shoppingcart.domain.projector

import com.shoppingcart.com.shoppingcart.domain.events.Event.*
import com.shoppingcart.com.shoppingcart.domain.projector.CartEntity
import com.shoppingcart.com.shoppingcart.domain.projector.CartItemEntity
import com.shoppingcart.com.shoppingcart.domain.projector.ShoppingCartProjectionRepository
import com.shoppingcart.com.shoppingcart.domain.projector.ShoppingCartProjector
import com.shoppingcart.domain.Invalid
import com.shoppingcart.domain.aggregate.Price
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.TestInstance
import org.mockito.Mockito.times
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class ShoppingCartProjectorTest {


    val repository: ShoppingCartProjectionRepository = mockk()
    val projector = ShoppingCartProjector(repository)
    val cartId = UUID.randomUUID()
    val cartEntity: CartEntity = CartEntity(cartId)
    val productId = UUID.randomUUID()
    val price = Price(10)


    @Test
    fun `should add product to cart if cart exists`() {

        every { repository.getCartEntity(cartId) } returns Optional.of(cartEntity)
        every { repository.saveCartItemEntity(any()) }.answers { nothing }
        projector.project(aProductAddedToCartEvent())
        verify { repository.saveCartItemEntity(any()); times(1) }

    }

    @Test
    fun `should create cart then add product to cart if cart does not exist`() {

        every { repository.getCartEntity(cartId) } returns Optional.empty()
        every { repository.saveCartItemEntity(any()) }.answers { nothing }
        every { repository.saveCartEntity(cartEntity) }.answers { nothing }

        projector.project(aProductAddedToCartEvent())

        verify { repository.saveCartItemEntity(any()); times(1) }
        verify { repository.saveCartEntity(cartEntity); times(1) }

    }


    @Test
    fun `should remove product from cart`() {

        every { repository.removeCartItem(any()) }.answers { nothing }
        every { repository.getCartEntityItemsOfProduct(cartId, productId) } returns getCartEntityItems()
        projector.project(aProductRemovedFromCartEvent())
        verify { repository.removeCartItem(any()); times(2) }
    }


    @Test
    fun `should return invalid when product is not in cart `() {

        every { repository.getCartEntityItemsOfProduct(cartId, productId) } returns emptyList()
        val result = projector.project(aProductRemovedFromCartEvent())
        assertTrue(result is Invalid)
    }


    @Test
    fun `should add  entity item  for each increased amount `() {

        every { repository.getCartEntityItemsOfProduct(cartId, productId) } returns getCartEntityItems()
        every { repository.saveCartItemEntity(any()) }.answers { nothing }
        projector.project(anAmountOfProductChangedEvent(4))
        verify { repository.saveCartItemEntity(any()); times(2) }
    }

    @Test
    fun `should remove  entity item  for each decreased amount `() {

        every { repository.getCartEntityItemsOfProduct(cartId, productId) } returns getCartEntityItems()
        every { repository.removeCartItem(any()) }.answers { nothing }
        projector.project(anAmountOfProductChangedEvent(1))
        verify { repository.removeCartItem(any()); times(1) }
    }

    private fun aProductAddedToCartEvent(): ProductAddedToCartEvent {
        return ProductAddedToCartEvent(productId, price.price, cartId)
    }

    private fun aProductRemovedFromCartEvent(): ProductRemovedFromCartEvent {
        return ProductRemovedFromCartEvent(productId, cartId)
    }

    private fun anAmountOfProductChangedEvent(amount: Int): AmountOfProductChangedEvent {
        return AmountOfProductChangedEvent(cartId, productId, amount)
    }

    private fun getCartEntityItems(): List<CartItemEntity> {
        val cartItems = mutableListOf<CartItemEntity>()
        cartItems.add(CartItemEntity(UUID.randomUUID(), cartId, productId, 10))
        cartItems.add(CartItemEntity(UUID.randomUUID(), cartId, productId, 10))
        return cartItems

    }


}