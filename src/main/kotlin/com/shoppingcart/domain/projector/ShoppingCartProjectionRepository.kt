package com.shoppingcart.com.shoppingcart.domain.projector

import java.util.*

interface ShoppingCartProjectionRepository {

    fun getCartEntity(id: UUID): Optional<CartEntity>

    fun getCartItemEntity(id: UUID): Optional<CartItemEntity>

    fun getCartEntityItemsOfProduct(cartId: UUID, productId:UUID): List<CartItemEntity>

    fun saveCartEntity(cartEntity: CartEntity)

    fun saveCartItemEntity(cartItemEntity: CartItemEntity)

    fun removeCartItem(id:UUID)

}