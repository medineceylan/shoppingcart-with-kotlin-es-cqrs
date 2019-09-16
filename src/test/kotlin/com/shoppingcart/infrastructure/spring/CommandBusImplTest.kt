package com.shoppingcart.infrastructure.spring

import com.shoppingcart.application.Command
import com.shoppingcart.application.CommandListener
import com.shoppingcart.com.shoppingcart.application.CommandBus
import com.shoppingcart.com.shoppingcart.infrastructure.spring.CommandBusImpl
import com.shoppingcart.infrastructure.spring.CommandBusImplTest.CommandHandlerSample
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit.SECONDS

@RunWith(SpringRunner::class)
@ContextConfiguration(classes = [CommandBusImpl::class, CommandHandlerSample::class])
class CommandBusImplTest {

    @Autowired
    private lateinit var commandHandlerSample: CommandHandlerSample

    @Autowired
    private lateinit var commandBus: CommandBus

    companion object {

        val productId = UUID.randomUUID()
        val cartId = UUID.randomUUID()
        val latch = CountDownLatch(1)
    }

    @Test
    fun `should send and receive command`() {

        commandBus.send(Command.AddProductToCartCommand(productId, 10, cartId))
        latch.await(5, SECONDS)
        assertThat(commandHandlerSample.invoked).isTrue()
    }

    @Service
    class CommandHandlerSample {

        var invoked = false

        @CommandListener
        fun handle(command: Command.AddProductToCartCommand) {
            invoked = true
            assertThat(command.productId).isEqualTo(productId)
            latch.countDown()
        }
    }
}
