package com.shoppingcart.application

import java.util.*

sealed class Command {

    data class AddProductToCartCommand(val productId: UUID, val price: Int, val cartId: UUID) : Command()
    data class RemoveProductFromCartCommand(val productId: UUID, val cartId: UUID) : Command()
    data class ChangeAmountOfProductCommand(val cartId: UUID, val productId: UUID, val amount: Int) : Command()
    data class CalculateTotalPriceCommand(val cartId: UUID) : Command()

}