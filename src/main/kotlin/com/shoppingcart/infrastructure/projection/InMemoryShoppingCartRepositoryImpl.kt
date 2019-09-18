package com.shoppingcart.infrastructure.projection

import arrow.Kind
import com.shoppingcart.com.shoppingcart.domain.projector.CartEntity
import com.shoppingcart.com.shoppingcart.domain.projector.CartItemEntity
import com.shoppingcart.com.shoppingcart.domain.projector.ShoppingCartProjectionRepository
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class InMemoryShoppingCartRepositoryImpl : ShoppingCartProjectionRepository {

    private val carts=ConcurrentHashMap<UUID, MutableList<CartItemEntity>>()




    override fun getCartEntityItemsOfProduct(cartId: UUID, productId: UUID): List<CartItemEntity> {

        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun removeCartItem(id: UUID) {

    }

    override fun saveCartItemEntity(cartItemEntity: CartItemEntity) {


    }

    override fun saveCartEntity(cartEntity: CartEntity) {

        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getCartEntity(id: UUID): Optional<CartEntity> {

        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getCartItemEntity(id: UUID): Optional<CartItemEntity> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}