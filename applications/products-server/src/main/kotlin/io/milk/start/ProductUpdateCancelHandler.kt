package io.milk.start

import com.rabbitmq.client.CancelCallback

class ProductUpdateCancelHandler : CancelCallback {
    override fun handle(consumerTag: String) {
    }
}
