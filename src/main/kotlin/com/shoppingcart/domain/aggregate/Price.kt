package com.shoppingcart.domain.aggregate

@ValueObject
class Price(var price: Int) {

    fun multiply(multiplier: Int): Price {
        return Price(price * multiplier)
    }

    fun add(adder: Int): Int {
        return adder.plus(price)
    }

}