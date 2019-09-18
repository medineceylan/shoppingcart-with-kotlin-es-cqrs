package com.shoppingcart.com.shoppingcart.domain.events

import java.util.*

typealias EventList = List<Event>

sealed class Event(var domainEntityId: UUID) {

    data class ProductAddedToCartEvent(val productId: UUID, val price: Int, val cartId: UUID) : Event(cartId)
    data class ProductRemovedFromCartEvent(val productId: UUID, val cartId: UUID) : Event(productId)
    data class TotalPriceCalculatedEvent(val cartId: UUID) : Event(cartId)
    data class AmountOfProductChangedEvent(val cartId: UUID, val productId: UUID, val amount: Int) : Event(cartId)
    data class ProductNotFoundInCartEvent(val productId: UUID, val cartId: UUID) : Event(cartId)

}