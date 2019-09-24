package com.shoppingcart.domain.aggregate

import com.shoppingcart.domain.aggregate.annotations.ValueObject

@ValueObject
class Price(var price: Int) {

    fun multiply(multiplier: Int): Price {
        return Price(price * multiplier)
    }

    fun add(adder: Int): Int {
        return adder.plus(price)
    }

}