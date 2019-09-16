package com.shoppingcart.com.shoppingcart.domain.projector

import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class CartItemEntity(
        @Id
        @GeneratedValue(generator = "UUID")
        val id: UUID,
        val cartId: UUID,
        val productId: UUID,
        val price: Int
)