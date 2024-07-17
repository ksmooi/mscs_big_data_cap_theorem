package io.milk.httpclient

import io.milk.workflow.WorkFinder
import org.slf4j.LoggerFactory

class PurchaseGenerator : WorkFinder<PurchaseTask> {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    override fun findRequested(name: String): List<PurchaseTask> {
        val random = (1..4).random()

        logger.info("someone purchased some milk!")

        return mutableListOf(PurchaseTask(105442, "milk", random))
    }

    override fun markCompleted(info: PurchaseTask) {
        logger.info("completed purchase")
    }
}
