package com.shoppingcart.domain.aggregate

import arrow.core.Failure
import arrow.core.Success
import arrow.core.Try
import com.shoppingcart.application.Command.*
import com.shoppingcart.com.shoppingcart.domain.events.Event
import com.shoppingcart.com.shoppingcart.domain.events.Event.*
import com.shoppingcart.domain.DomainError
import com.shoppingcart.domain.DomainError.*
import com.shoppingcart.domain.Invalid
import com.shoppingcart.domain.Valid
import com.shoppingcart.domain.Validated
import java.util.*

typealias CmdResult = Validated<DomainError, UUID>
typealias  ValidationResult = Validated<DomainError, UUID>

@AggregateRoot
@Entity
class CartEntity(var aggregateRootId: UUID) {

    var cartItems = mutableMapOf<UUID, CartItemEntity>()
    var totalPrice: Int = 0

    fun applyAll(vararg events: Event): CartEntity {
        return applyAll(events.toList())
    }

    fun applyAll(events: List<Event>): CartEntity {

        events.forEach {
            when (it) {
                is ProductAddedToCartEvent -> apply(it)
                is ProductRemovedFromCartEvent -> apply(it)
                is AmountOfProductChangedEvent -> apply(it)
                is TotalPriceCalculatedEvent -> apply(it)
            }
        }
        return this
    }

    fun handle(command: AddProductToCartCommand): CmdResult {

        if (command.price <= 0) {
            return Invalid(AmountMustBePositiveException(command.price, "Price must be greater than 0!!"));
        }
        return Valid(aggregateRootId)
    }

    fun handle(command: ChangeAmountOfProductCommand): CmdResult {

        if (command.amount <= 0) {
            return Invalid(AmountMustBePositiveException(command.amount, "Amount must be greater than 0!!"));
        }
        return Valid(aggregateRootId)
    }

    fun apply(event: ProductAddedToCartEvent): Unit {

        aggregateRootId = event.cartId

        val result = find(event.productId)
        when (result) {
            is Success -> result.value.add(1)
            is Failure -> {
                cartItems.put(event.productId, CartItemEntity(event.productId, 1, Price(event.price)))
            }
        }
    }

    fun apply(event: AmountOfProductChangedEvent): Try<CartItemEntity> {

        val result = find(event.productId)

        when (result) {
            is Success -> {
                val cartItemEntity = result.value.changeAmount(event.amount)
                cartItems.put(cartItemEntity.productId, cartItemEntity)
            }

        }

        return result
    }

    fun apply(event: TotalPriceCalculatedEvent): Unit {

        val prices = this.cartItems.values.map { item -> item.calculatePrice() }.toCollection(mutableListOf())
        this.totalPrice = sum(prices)

    }

    fun apply(event: ProductRemovedFromCartEvent): Try<CartItemEntity> {

        val result = find(event.productId)

        when (result) {
            is Success -> this.cartItems.remove(result.value.productId)
        }

        return result
    }

    private fun validateProduct(productId: UUID): ValidationResult {

        val result = find(productId)

        when (result) {
            is Success -> return Valid(productId)
            is Failure -> return Invalid(ProductNotInCartException(productId, "Product not in the cart"))

        }
    }


    private fun find(productId: UUID): Try<CartItemEntity> {

        val cartItem = cartItems.values.filter { item -> item.productId == productId }

        if (cartItem.isNotEmpty()) {
            return Success(cartItem[0])
        }

        return Failure(ProductNotInCartException(productId, " Product is not in cart!! "))
    }

    private fun sum(prices: MutableList<Price>): Int {
        return prices.fold(0) { sum, element -> sum.plus(element.price) }
    }


}




