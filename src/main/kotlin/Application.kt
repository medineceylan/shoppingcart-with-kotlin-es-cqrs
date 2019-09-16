package com.shoppingcart

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import springfox.documentation.swagger2.annotations.EnableSwagger2
import org.springframework.web.client.RestTemplate

@SpringBootApplication
@EnableAutoConfiguration
@EnableSwagger2
open class Application

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}

@Bean
fun restTemplate(): RestTemplate {
    return RestTemplate()
}
