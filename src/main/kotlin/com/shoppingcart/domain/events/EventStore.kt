package com.shoppingcart.com.shoppingcart.domain.events

import java.util.*

interface EventStore {

    fun save(event: Event)

    fun saveAll(events: List<Event>) {
        events.forEach(this::save)
    }

    fun allForHistory(cartId: UUID): List<Event>

    fun exists(cartId: UUID): Boolean

}






