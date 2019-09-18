package com.shoppingcart.com.shoppingcart.domain.projector

import arrow.effects.IO
import com.shoppingcart.com.shoppingcart.domain.events.Event.*
import com.shoppingcart.domain.DomainError
import com.shoppingcart.domain.DomainError.ProductNotInCartException
import com.shoppingcart.domain.Invalid
import com.shoppingcart.domain.Valid
import com.shoppingcart.domain.Validated
import java.util.*

typealias ProjectorResult = Validated<DomainError, UUID>


class ShoppingCartProjector(private val repository: ShoppingCartProjectionRepository) {


    fun project(event: ProductAddedToCartEvent): Unit {

        val shoppingCart = repository.getCartEntity(event.cartId)

        if (!shoppingCart.isPresent) {
            val cartEntity = CartEntity(event.cartId)
            repository.saveCartEntity(cartEntity)
        }

        val cartItemEntity = CartItemEntity(UUID.randomUUID(), event.cartId, event.productId, event.price)
        repository.saveCartItemEntity(cartItemEntity)

    }

    fun project(event: ProductRemovedFromCartEvent): ProjectorResult {

        val shoppingCartItems = repository.getCartEntityItemsOfProduct(event.cartId, event.productId)

        if (shoppingCartItems.isEmpty()) {
            return Invalid(ProductNotInCartException(event.productId, "there isn't any product of the cart with this id"))
        }

        shoppingCartItems.forEach {
            repository.removeCartItem(it.id)
        }

        return Valid(event.productId)
    }

    fun project(event: AmountOfProductChangedEvent): Unit {


        val shoppingCartItems = repository.getCartEntityItemsOfProduct(event.cartId, event.productId)


        if (event.amount < shoppingCartItems.size) {
            removeCartItems(shoppingCartItems, event)
        } else {
            addCartItems(shoppingCartItems, event)
        }

    }



    private fun removeCartItems(cartItems: List<CartItemEntity>, event: AmountOfProductChangedEvent) {

        var quantity = event.amount
        while (quantity < cartItems.size) {
            repository.removeCartItem(cartItems[quantity].id)
            quantity++
        }

    }

    private fun addCartItems(cartItems: List<CartItemEntity>, event: AmountOfProductChangedEvent) {

        var quantity = event.amount
        while (quantity >= cartItems.size) {
            val cartItem = CartItemEntity(UUID.randomUUID(), event.productId, event.cartId, cartItems[0].price)
            repository.saveCartItemEntity(cartItem)
            quantity--
        }


    }


}