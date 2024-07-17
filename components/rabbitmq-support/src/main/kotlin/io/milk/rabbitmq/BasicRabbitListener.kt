package io.milk.rabbitmq

import com.rabbitmq.client.*

class BasicRabbitListener(
    private val queue: String,
    private val delivery: ChannelDeliverCallback,
    private val cancel: CancelCallback,
    private val autoAck: Boolean
) {
    private val factory = ConnectionFactory().apply { host = "localhost" }

    fun start() {
        val connection: Connection = factory.newConnection()
        val channel: Channel = connection.createChannel()
        delivery.setChannel(channel)

        channel.basicConsume(queue, autoAck, { consumerTag, message ->
            delivery.handle(consumerTag, message)
        }, cancel)
    }
}
