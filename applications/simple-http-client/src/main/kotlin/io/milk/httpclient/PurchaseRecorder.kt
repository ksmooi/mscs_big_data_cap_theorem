package io.milk.httpclient

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.milk.workflow.Worker
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import org.slf4j.LoggerFactory

class PurchaseRecorder(
        private val httpClient: OkHttpClient,
        private val urlString: String,
        override val name: String = "sales-worker"
) : Worker<PurchaseTask> {
    private val logger = LoggerFactory.getLogger(this.javaClass)
    private val mapper: ObjectMapper = ObjectMapper().registerKotlinModule()
    private val media = "application/json; charset=utf-8".toMediaType()

    override fun execute(task: PurchaseTask) {
        try {
            val json = mapper.writeValueAsString(task)
            val body = json.toRequestBody(media)
            val ok = okhttp3.Request.Builder().url(urlString).post(body).build()

            logger.info("decrementing the {} quantity by {} for product_id={}", task.name, task.amount, task.id)

            httpClient.newCall(ok).execute().close()
        } catch (e: Exception) {
            logger.error(
                    "shoot, failed to decrement the {} quantity by {} for product_id={}",
                    task.name,
                    task.amount,
                    task.id
            )
            e.printStackTrace()
        }
    }
}
