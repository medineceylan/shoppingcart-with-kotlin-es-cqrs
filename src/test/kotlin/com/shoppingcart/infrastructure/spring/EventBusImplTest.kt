package com.shoppingcart.infrastructure.spring

import com.shoppingcart.com.shoppingcart.domain.events.Event.ProductAddedToCartEvent
import com.shoppingcart.com.shoppingcart.domain.events.EventBus
import com.shoppingcart.com.shoppingcart.infrastructure.spring.SpringEventBus
import com.shoppingcart.infrastructure.EventListener
import com.shoppingcart.infrastructure.spring.EventBusTest.EventHandlerSample
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


@RunWith(SpringRunner::class)
@ContextConfiguration(classes = [SpringEventBus::class, EventHandlerSample::class])
class EventBusTest {

    @Autowired
    private lateinit var eventBus: EventBus

    @Autowired
    private lateinit var eventHandler: EventHandlerSample

    companion object {
        val productId = UUID.randomUUID()
        val cartId = UUID.randomUUID()
        val latch = CountDownLatch(1)
    }

    @Test
    fun `should send and receive command`() {
        eventBus.send(ProductAddedToCartEvent(productId, 10, cartId))
        latch.await(5, TimeUnit.SECONDS)
        assertThat(eventHandler.invoked).isTrue()
    }

    @Service
    class EventHandlerSample {

        var invoked = false

        @EventListener
        fun handle(event: ProductAddedToCartEvent) {
            invoked = true
            assertThat(event.productId).isEqualTo(productId)
            latch.countDown()
        }
    }


}
