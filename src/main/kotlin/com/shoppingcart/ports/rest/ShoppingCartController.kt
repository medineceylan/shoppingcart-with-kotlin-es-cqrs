package com.shoppingcart.com.shoppingcart.ports.rest

import com.shoppingcart.application.Command.*
import com.shoppingcart.com.shoppingcart.application.CommandBus
import com.shoppingcart.domain.Valid
import com.shoppingcart.domain.aggregate.CmdResult
import com.shoppingcart.ports.model.Request.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController("/shopping-cart")
class ShoppingCartController(private val commandBus: CommandBus) {

    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    @PostMapping("/add-product")
    fun addProductToCart(@RequestBody request: AddProductToCartRequest): ResponseEntity<CmdResult> {

        // validate request

        commandBus.send(AddProductToCartCommand(
                productId = request.productId,
                price = request.price,
                cartId = request.cartId))

        return ResponseEntity(Valid(request.cartId), HttpStatus.OK)

    }

    @PostMapping("/remove-product")
    fun removeProductFromCart(@RequestBody request: RemoveProductFromCartRequest): ResponseEntity<CmdResult> {
        //validate request

        commandBus.send(RemoveProductFromCartCommand(
                productId = request.productId,
                cartId = request.cartId))

        return ResponseEntity(Valid(request.cartId), HttpStatus.OK)

    }

    @GetMapping("/calculate-price")
    fun calculateTotalPrice(@RequestBody request: CalculateTotalPriceRequest): ResponseEntity<CmdResult> {

        //validate request

        commandBus.send(CalculateTotalPriceCommand(cartId = request.cartId))
        return ResponseEntity(Valid(request.cartId), HttpStatus.OK)

    }

    @PatchMapping("/change-product-amount")
    fun changeAmoutOfProduct(@RequestBody request: ChangeAmountOfProductRequest): ResponseEntity<CmdResult> {
        //validate request

        commandBus.send(ChangeAmountOfProductCommand(cartId = request.cartId, productId = request.productId, amount = request.amount))
        return ResponseEntity(Valid(request.cartId), HttpStatus.OK)

    }


}




