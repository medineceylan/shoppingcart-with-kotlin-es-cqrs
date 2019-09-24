package com.shoppingcart.com.shoppingcart.application

import com.shoppingcart.application.Command
import com.shoppingcart.com.shoppingcart.infrastructure.spring.CommandList

interface CommandBus {

    fun send(command: Command)

    fun sendAll(commands: CommandList) {
        commands.forEach(this::send)
    }
}
