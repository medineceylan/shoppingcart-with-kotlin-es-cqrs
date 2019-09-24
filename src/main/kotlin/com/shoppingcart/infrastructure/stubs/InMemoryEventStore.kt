package com.shoppingcart.com.shoppingcart.infrastructure.stubs

import com.shoppingcart.com.shoppingcart.domain.events.Event
import com.shoppingcart.com.shoppingcart.domain.events.EventList
import com.shoppingcart.com.shoppingcart.domain.events.EventStore
import org.springframework.stereotype.Service
import java.util.*


@Service
class InMemoryEventStore : EventStore {

    private val events = mutableListOf<Event>()

    override fun save(event: Event) {
        events.add(event)
    }

    override fun loadHistory(cartId: UUID):EventList {
        return events.filter { item->item.domainEntityId==cartId }

    }

    override fun exists(id: UUID): Boolean {
        return loadHistory(id).isNotEmpty()
    }

    fun clear() {
        events.clear()
    }
}