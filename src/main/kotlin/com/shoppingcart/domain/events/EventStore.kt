package com.shoppingcart.com.shoppingcart.domain.events

import java.util.*

interface EventStore {


    fun save(event: Event)

    fun saveAll(events:EventList) {
        events.forEach(this::save)
    }

    fun loadHistory(cartId: UUID): EventList

    fun exists(cartId: UUID): Boolean

}






