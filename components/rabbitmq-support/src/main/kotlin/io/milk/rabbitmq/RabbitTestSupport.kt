package io.milk.rabbitmq

import com.rabbitmq.client.ConnectionFactory

class RabbitTestSupport {
    private val factory = ConnectionFactory().apply { host = "localhost" }

    fun purge(queue: String) {
        factory.newConnection().use { connection ->
            connection.createChannel().use { channel ->
                channel.queuePurge(queue)
            }
        }
    }

    fun waitForConsumers(queue: String) {
        factory.newConnection().use { connection ->
            connection.createChannel().use { channel ->
                var previousMessageCount = -1

                while (true) {
                    val queueDeclare = channel.queueDeclarePassive(queue)
                    val currentMessageCount = queueDeclare.messageCount
                    val consumerCount = queueDeclare.consumerCount

                    if (currentMessageCount == 0 && consumerCount == 0) {
                        break
                    }

                    if (previousMessageCount != currentMessageCount) {
                        previousMessageCount = currentMessageCount
                        Thread.sleep(100) // Wait for 100ms before polling again
                    } else {
                        break
                    }
                }
            }
        }
    }
}
