package io.milk.httpclient

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.jetty.*
import io.milk.workflow.WorkScheduler
import okhttp3.OkHttpClient
import java.util.*

fun Application.module() {
    install(Routing) {
        get("/") {
            call.respondText { "ok!" }
        }
    }

    val urlString: String = System.getenv("PRODUCTS_SERVER") ?: "http://localhost:8081/api/v1/products"
    val httpClient = OkHttpClient().newBuilder().build()
    val workers = (1..4).map {
        PurchaseRecorder(httpClient, urlString)
    }
    val scheduler = WorkScheduler(PurchaseGenerator(), workers, 10)
    scheduler.start()
}

fun main() {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    val port = System.getenv("PORT")?.toInt() ?: 8082
    embeddedServer(Jetty, port, module = Application::module).start()
}
