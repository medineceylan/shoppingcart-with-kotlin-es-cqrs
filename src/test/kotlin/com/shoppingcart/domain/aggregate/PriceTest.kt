package com.shoppingcart.domain.aggregate

import org.junit.Test
import kotlin.test.assertTrue

internal class PriceTest {

    companion object {
        val price = Price(10)
    }

    @Test
    fun `should multiply price`() {
        val price= price.multiply(5)

        assertTrue { price.price == 50 }

    }

    @Test
    fun `should add price`() {
        val price= price.add(5)

        assertTrue { price == 15 }

    }

}