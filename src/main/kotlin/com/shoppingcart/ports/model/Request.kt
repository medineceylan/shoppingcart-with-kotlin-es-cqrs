package com.shoppingcart.ports.model

import java.util.*

sealed class Request {

    data class AddProductToCartRequest(var productId: UUID, var price: Int, var cartId: UUID) : Request()
    data class RemoveProductFromCartRequest(var productId: UUID, var cartId: UUID) : Request()
    data class ChangeAmountOfProductRequest(val cartId: UUID, val productId: UUID, val amount: Int) : Request()
    data class CalculateTotalPriceRequest(val cartId: UUID) : Request()

}
