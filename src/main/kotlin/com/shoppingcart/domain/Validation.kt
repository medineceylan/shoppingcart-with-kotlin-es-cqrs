package com.shoppingcart.domain

sealed class Validation<out A, out B>

data class Invalid<A>(val err: A) : Validation<A, Nothing>()
data class Valid<B>(val value: B) : Validation<Nothing, B>()