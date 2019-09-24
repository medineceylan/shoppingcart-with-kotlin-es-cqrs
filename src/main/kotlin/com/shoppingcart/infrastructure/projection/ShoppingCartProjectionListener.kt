package com.shoppingcart.infrastructure.projection

import com.shoppingcart.com.shoppingcart.domain.events.Event.*
import com.shoppingcart.com.shoppingcart.domain.projector.ShoppingCartProjector
import com.shoppingcart.domain.Invalid
import com.shoppingcart.infrastructure.EventListener
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class ShoppingCartProjectionListener(private val shoppingCartProjector: ShoppingCartProjector) {

    private val log: Logger = LoggerFactory.getLogger(this.javaClass)


    @EventListener
    fun handle(event: ProductAddedToCartEvent) {
        shoppingCartProjector.project(event)
    }

    @EventListener
    fun handle(event: ProductRemovedFromCartEvent) {

        when (val result = shoppingCartProjector.project(event)) {
            is Invalid -> log.error(result.err.message)
        }

    }

    @EventListener
    fun handle(event: AmountOfProductChangedEvent) {
        shoppingCartProjector.project(event)
    }

}
