package io.milk.rabbitmq

import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory

class BasicRabbitConfiguration(
    private val exchange: String,
    private val queue: String,
    private val routingKey: String
) {
    private val factory = ConnectionFactory().apply { host = "localhost" }

    fun setUp() {
        factory.newConnection().use { connection ->
            connection.createChannel().use { channel ->
                channel.exchangeDeclare(exchange, "direct", true)
                channel.queueDeclare(queue, true, false, false, null)
                channel.queueBind(queue, exchange, routingKey)
            }
        }
    }
}
