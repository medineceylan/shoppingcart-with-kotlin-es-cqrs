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
    private val occurredEvents: MutableList<Event> = mutableListOf()

    @CommandListener
    fun handle(command: AddProductToCartCommand) {
        val cart = applyHistoryAndGetCart(command.cartId)

        when (val result = cart.handle(command)) {
            is Valid -> {
                val newEvent = ProductAddedToCartEvent(productId = command.productId, price = command.price, cartId = command.cartId)
                cart.apply(newEvent)
                raise(newEvent)
                storeAndPublishEvents()
            }
            is Invalid -> log.error(result.err.message)
        }
    }

    @CommandListener
    fun handle(command: RemoveProductFromCartCommand) {

        val cart = applyHistoryAndGetCart(command.cartId)
        val productRemovedFromCartEvent = ProductRemovedFromCartEvent(productId = command.productId, cartId = command.cartId)

        when (cart.apply(productRemovedFromCartEvent)) {
            is Success -> raise(productRemovedFromCartEvent)
            is Failure -> raise(ProductNotFoundInCartEvent(command.productId, command.cartId))
        }
        storeAndPublishEvents()

    }

    @CommandListener
    fun handle(command: ChangeAmountOfProductCommand) {

        val cart = applyHistoryAndGetCart(command.cartId)

        when (val cmdResult = cart.handle(command)) {
            is Valid -> {
                val productChangedEvent = AmountOfProductChangedEvent(cartId = command.cartId, productId = command.productId, amount = command.amount)

                when (cart.apply(productChangedEvent)) {
                    is Success -> raise(productChangedEvent)
                    is Failure -> raise(ProductNotFoundInCartEvent(command.productId, command.cartId))
                }
                storeAndPublishEvents()
            }
            is Invalid -> log.error(cmdResult.err.message)
        }

    }

    @CommandListener
    fun handle(command: CalculateTotalPriceCommand) {

        val cart = applyHistoryAndGetCart(command.cartId)
        val newEvent = TotalPriceCalculatedEvent(cartId = command.cartId)
        cart.apply(newEvent)
        raise(newEvent)
        storeAndPublishEvents()
    }


    private fun storeAndPublishEvents() {

        val newlyOccurredEvents = getAndClearOccurredEvents()
        if (newlyOccurredEvents.isEmpty()) return

        eventBus.sendAll(newlyOccurredEvents)
        eventStore.saveAll(newlyOccurredEvents)
    }


    private fun applyHistoryAndGetCart(cartId: UUID): CartEntity {

        val cart = CartEntity(cartId)
        cart.applyAll(eventStore.allForHistory(cartId))

        return cart

    }

    private fun getAndClearOccurredEvents(): List<Event> {

        val events = this.occurredEvents.toMutableList()
        this.occurredEvents.clear()
        log.info("Return occurred domain events. [numberOfEvents=${events.size}]")
        return events
    }

    fun getOccurredEvents(): List<Event> {
        return this.occurredEvents.toMutableList()
    }

    private fun raise(event: Event) {
        occurredEvents.add(event)
        log.info("Raised new domain event. [type=${event::class.simpleName}]")
    }


}