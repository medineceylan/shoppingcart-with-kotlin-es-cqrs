package com.shoppingcart.application

import arrow.core.Failure
import arrow.core.Success
import com.shoppingcart.application.Command.*
import com.shoppingcart.com.shoppingcart.domain.events.Event
import com.shoppingcart.com.shoppingcart.domain.events.Event.*
import com.shoppingcart.com.shoppingcart.domain.events.EventBus
import com.shoppingcart.com.shoppingcart.domain.events.EventStore
import com.shoppingcart.domain.Invalid
import com.shoppingcart.domain.Valid
import com.shoppingcart.domain.aggregate.CartEntity
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

@Service
class ShoppingCartCommandHandler(private val eventBus: EventBus, private val eventStore: EventStore) {

    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    @CommandListener
    fun handle(command: AddProductToCartCommand) {

        val cart = applyHistoryAndPrepareCart(command.cartId)

        when (val result = cart.handle(command)) {
            is Valid -> {
                val newEvent = ProductAddedToCartEvent(productId = command.productId, price = command.price, cartId = command.cartId)
                cart.apply(newEvent)
                storeAndPublishEvents(newEvent)
            }
            is Invalid -> log.error(result.err.message)
        }
    }

    @CommandListener
    fun handle(command: RemoveProductFromCartCommand) {

        val cart = applyHistoryAndPrepareCart(command.cartId)
        val productRemovedFromCartEvent = ProductRemovedFromCartEvent(productId = command.productId, cartId = command.cartId)

        when (cart.apply(productRemovedFromCartEvent)) {
            is Success -> storeAndPublishEvents(productRemovedFromCartEvent)
            is Failure -> storeAndPublishEvents(ProductNotFoundInCartEvent(command.productId, command.cartId))
        }

    }

    @CommandListener
    fun handle(command: ChangeAmountOfProductCommand) {

        val cart = applyHistoryAndPrepareCart(command.cartId)

        when (val cmdResult = cart.handle(command)) {
            is Valid -> {
                val productChangedEvent = AmountOfProductChangedEvent(cartId = command.cartId, productId = command.productId, amount = command.amount)

                when (cart.apply(productChangedEvent)) {
                    is Success -> storeAndPublishEvents(productChangedEvent)
                    is Failure -> storeAndPublishEvents(ProductNotFoundInCartEvent(command.productId, command.cartId))
                }
            }
            is Invalid -> log.error(cmdResult.err.message)
        }

    }

    @CommandListener
    fun handle(command: CalculateTotalPriceCommand) {

        val cart = applyHistoryAndPrepareCart(command.cartId)
        val newEvent = TotalPriceCalculatedEvent(cartId = command.cartId)
        cart.apply(newEvent)
        storeAndPublishEvents(newEvent)
    }


    private fun storeAndPublishEvents(event: Event) {

        log.info("Raised new domain event. [type=${event::class.simpleName}]")
        eventBus.send(event)
        eventStore.save(event)
    }


    private fun applyHistoryAndPrepareCart(cartId: UUID): CartEntity {

        val cart = CartEntity(cartId)
        cart.applyAll(eventStore.loadHistory(cartId))
        return cart

    }

}