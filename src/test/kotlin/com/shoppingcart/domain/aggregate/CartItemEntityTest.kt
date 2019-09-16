package com.shoppingcart.domain.aggregate

import org.junit.Before
import org.junit.Test
import java.util.*
import kotlin.test.assertTrue

internal class CartItemEntityTest {

    companion object {
        val productId = UUID.randomUUID()
        val quantity = 1
        val unitPrice = Price(10)

        var cartItemEntity = CartItemEntity(productId, quantity, unitPrice)
    }

    @Before
    fun startUp() {
        cartItemEntity = CartItemEntity(productId, quantity, unitPrice)
    }

    @Test
    fun `should increment quantity`() {

        cartItemEntity.add(2)
        assertTrue { cartItemEntity.quantity == 3 }

    }


    @Test
    fun `should change amount`() {

        cartItemEntity.changeAmount(15)
        assertTrue { cartItemEntity.quantity == 15 }

    }

    @Test
    fun `should calculate price`() {

        val cartItemEntity = CartItemEntity(productId, 2, Price(15))
        val result = cartItemEntity.calculatePrice()
        assertTrue { result.price == 30 }
    }


}