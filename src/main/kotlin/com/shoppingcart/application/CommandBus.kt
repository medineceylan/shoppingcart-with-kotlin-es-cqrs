package com.shoppingcart.com.shoppingcart.application

import com.shoppingcart.application.Command

interface CommandBus {
    fun send(command: Command)

    fun sendAll(commands: List<Command>) {
        commands.forEach(this::send)
    }
}
