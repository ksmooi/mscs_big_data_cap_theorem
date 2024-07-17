package io.milk.start

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.rabbitmq.client.Channel
import com.rabbitmq.client.DeliverCallback
import com.rabbitmq.client.Delivery
import io.milk.products.ProductService
import io.milk.products.PurchaseInfo
import io.milk.rabbitmq.ChannelDeliverCallback
import org.slf4j.LoggerFactory

class ProductUpdateHandler(private val service: ProductService) : ChannelDeliverCallback {
    private val logger = LoggerFactory.getLogger(this.javaClass)
    private val mapper = ObjectMapper().registerKotlinModule()
    private var channel: Channel? = null

    override fun setChannel(channel: Channel) {
        this.channel = channel
    }

    override fun handle(consumerTag: String, message: Delivery) {
        val purchase = mapper.readValue<PurchaseInfo>(message.body)

        logger.info(
            "received event. purchase for {}, quantity={}, product_id={}",
            purchase.name,
            purchase.amount,
            purchase.id
        )
        service.decrementBy(purchase) // Replaced update with decrementBy
    }
}
