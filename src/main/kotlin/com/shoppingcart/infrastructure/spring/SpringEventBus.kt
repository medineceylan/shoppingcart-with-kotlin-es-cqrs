package com.shoppingcart.com.shoppingcart.infrastructure.spring

import com.shoppingcart.com.shoppingcart.domain.events.Event
import com.shoppingcart.com.shoppingcart.domain.events.EventBus
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class SpringEventBus(private val publisher: ApplicationEventPublisher) : EventBus {

    override fun send(event: Event) {
        publisher.publishEvent(event)
    }
}