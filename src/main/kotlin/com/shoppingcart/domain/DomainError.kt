package com.shoppingcart.domain


import java.util.*

sealed class DomainError(override var message: String) : Exception(message) {

    data class ProductNotInCartException(val productId: UUID, val error: String) : DomainError(error)

    data class AmountMustBePositiveException(val amount: Int, val error: String) : DomainError(error)

    data class InvalidCartException(val cartId: UUID, val error: String) : DomainError(error)

}