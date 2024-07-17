package io.milk.rabbitmq

import com.rabbitmq.client.CancelCallback

class ProductUpdateCancelHandler : CancelCallback {
    override fun handle(consumerTag: String) {
        // Implement cancellation handling logic
    }
}
