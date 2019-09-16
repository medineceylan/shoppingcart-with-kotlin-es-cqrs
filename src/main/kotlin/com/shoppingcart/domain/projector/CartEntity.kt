package com.shoppingcart.com.shoppingcart.domain.projector

import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id


@Entity
data class CartEntity(
        @Id
        @GeneratedValue(generator = "UUID")
        val id: UUID
)