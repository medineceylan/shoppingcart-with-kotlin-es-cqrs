
# Event Sourcing - CQRS poc with Kotlin

This is a sample project that demonstrates how shopping cart implemented by using:  


```
* Domain-Driven-Design 
* Event Sourcing
* CQRS
* Kotlin
* Arrow Library
* Hexagonal Architecture

```

__*Domain Modeling:*__

```
Because shopping cart has a couple of aggregate (product,cart,shipment,payment etc..) I just implemented
an aggregate which includes cart, cart item, price and productId. 

--My aggregate root is Cart
--Because Cart and CartItem have ids they are entities
--Price is a value object it doesn't have any identifier
--if product is in cart it's price doesn't change
--Because product is another aggregate root I seperated it from this aggregate and 
  I just put product id in this aggregate.
--Domain layer shouldn't use any framework 

```

I used Spring's event bus for publishing my events


__*A simple flow is like:*__

```
1. A command published via  CommandBus (for this example it is an end point)
2. CommandHandler:
  -gets all history of events from event store and applies them to aggregate root
  -validate command
     -if command is invalid writes error log and finished
     -if command is valid applies command to aggregate root and after aggregate root changed its state creates new events
     -stores all new events to event store and publish them via event bus
     
3. AggregateRoot:
    -validate command  
       if command is valid change its state and return valid to command handler
       if command is invalid return error to command handler

4. Projection Listener:
    -listens some events when events fired save current states to inmemory db via projector
    
  ```
    
__*need improvement:*__

```
    -request validation
    -in memory projection db
    -versioning
    -thread delegation
    -implementation of read model 
```

