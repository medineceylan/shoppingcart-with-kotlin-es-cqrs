package com.shoppingcart.domain.aggregate

import com.shoppingcart.domain.aggregate.annotations.Entity
import java.util.*

@Entity
class CartItemEntity(var productId: UUID, var quantity: Int = 0, var unitPrice: Price) {

    fun add(quantity: Int): CartItemEntity {
        this.quantity = this.quantity + quantity
        return this
    }

    fun changeAmount(amount: Int): CartItemEntity {
        this.quantity = amount
        return this
    }

    fun calculatePrice(): Price {
        return this.unitPrice.multiply(this.quantity);
    }


}