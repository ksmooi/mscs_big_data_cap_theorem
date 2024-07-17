package io.milk.start

import freemarker.cache.ClassTemplateLoader
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.freemarker.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.jackson.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.jetty.*
import io.milk.database.createDatasource
import io.milk.products.ProductDataGateway
import io.milk.products.ProductService
import io.milk.products.PurchaseInfo
import io.milk.products.ProductNotFoundException
import io.milk.products.InsufficientStockException
import io.milk.rabbitmq.BasicRabbitConfiguration
import io.milk.rabbitmq.BasicRabbitListener
import org.slf4j.LoggerFactory
import java.util.*

fun Application.module(jdbcUrl: String, username: String, password: String) {
    val logger = LoggerFactory.getLogger(this.javaClass)
    val dataSource = createDatasource(jdbcUrl, username, password)
    val productService = ProductService(ProductDataGateway(dataSource))

    install(DefaultHeaders)
    install(CallLogging)
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }
    install(ContentNegotiation) {
        jackson()
    }
    install(Routing) {
        get("/") {
            val products = productService.findAll()
            call.respond(FreeMarkerContent("index.ftl", mapOf("products" to products)))
        }
        post("/api/v1/products") {
            val purchase = call.receive<PurchaseInfo>()

            val currentInventory = productService.findBy(purchase.id)
            logger.info(
                "current inventory {}, quantity={}, product_id={}",
                currentInventory.name,
                currentInventory.quantity,
                currentInventory.id
            )

            logger.info(
                "received purchase for {}, quantity={}, product_id={}",
                purchase.name,
                purchase.amount,
                purchase.id
            )

            // Explanation of the Problem with Using `update`
            // Using `update` in the context of the `ProductService` class to handle inventory changes directly can be problematic due to potential dirty reads.
            // Dirty reads occur when a transaction reads data that has not yet been committed. In a concurrent environment, this can lead to inconsistencies and incorrect inventory counts.
            //
            // Here’s why using `update` can be problematic:
            // 1. Concurrency Issues:
            //    - Multiple transactions might attempt to update the same record simultaneously, leading to race conditions.
            //    - Without proper locking mechanisms, the inventory count might not reflect the actual state after concurrent updates.
            // 2. Data Integrity:
            //    - If the `update` method modifies the product quantity without ensuring the availability of sufficient stock, it might result in negative inventory counts.
            //    - Using `decrementBy` with proper validation ensures that the stock is checked before decrementing.
            // 3. Transactional Consistency:
            //    - Using `decrementBy` allows for better transactional control, ensuring that all necessary checks are performed within a single transaction.
            //    - It ensures that the stock is decremented only if there is sufficient quantity available, maintaining data consistency.

            try {
                productService.decrementBy(purchase) // Ensure proper stock handling
                call.respond(HttpStatusCode.Created)
            } catch (e: ProductNotFoundException) {
                call.respond(HttpStatusCode.NotFound, e.message ?: "Product not found")
            } catch (e: InsufficientStockException) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Insufficient stock")
            } catch (e: Exception) {
                logger.error("Error processing purchase", e)
                call.respond(HttpStatusCode.InternalServerError, "Error processing purchase")
            }
        }
        static("images") { resources("images") }
        static("style") { resources("style") }
    }

    BasicRabbitConfiguration(exchange = "products-exchange", queue = "products", routingKey = "auto").setUp()
    BasicRabbitListener(
        queue = "products",
        delivery = ProductUpdateHandler(productService),
        cancel = ProductUpdateCancelHandler(),
        autoAck = true,
    ).start()

    BasicRabbitConfiguration(exchange = "products-exchange", queue = "safer-products", routingKey = "safer").setUp()
    BasicRabbitListener(
        queue = "safer-products",
        delivery = SaferProductUpdateHandler(productService),
        cancel = ProductUpdateCancelHandler(),
        autoAck = false,  // Manual acknowledgement
    ).start()
}

fun main() {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    val port = System.getenv("PORT")?.toInt() ?: 8081
    val jdbcUrl = System.getenv("JDBC_DATABASE_URL") ?: "jdbc:postgresql://localhost:5432/milk_development"
    val username = System.getenv("JDBC_DATABASE_USERNAME") ?: "milk"
    val password = System.getenv("JDBC_DATABASE_PASSWORD") ?: "milk"

    // Print out the environment variables for debugging purposes
    println("JDBC_DATABASE_URL: $jdbcUrl")
    println("JDBC_DATABASE_USERNAME: $username")
    println("JDBC_DATABASE_PASSWORD: $password")

    requireNotNull(jdbcUrl) { "JDBC_DATABASE_URL must not be null" }
    requireNotNull(username) { "JDBC_DATABASE_USERNAME must not be null" }
    requireNotNull(password) { "JDBC_DATABASE_PASSWORD must not be null" }

    embeddedServer(Jetty, port, module = { module(jdbcUrl, username, password) }).start()
}
