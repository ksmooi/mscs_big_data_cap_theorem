package io.milk.rabbitmq

import com.rabbitmq.client.Channel
import com.rabbitmq.client.Delivery

interface ChannelDeliverCallback {
    fun setChannel(channel: Channel)
    fun handle(consumerTag: String, message: Delivery)
}
