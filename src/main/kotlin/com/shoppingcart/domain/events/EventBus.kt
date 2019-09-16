package com.shoppingcart.com.shoppingcart.domain.events

interface EventBus {

    fun send(event: Event)

    fun sendAll(events: List<Event>) {
        events.forEach(this::send)
    }
}
