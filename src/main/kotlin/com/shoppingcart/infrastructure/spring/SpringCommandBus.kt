package com.shoppingcart.com.shoppingcart.infrastructure.spring

import com.shoppingcart.application.Command
import com.shoppingcart.com.shoppingcart.application.CommandBus
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

typealias  CommandList=List<Command>


@Component
class SpringCommandBus(private val publisher: ApplicationEventPublisher) : CommandBus {

    override fun send(command: Command) {
        publisher.publishEvent(command)
    }

}