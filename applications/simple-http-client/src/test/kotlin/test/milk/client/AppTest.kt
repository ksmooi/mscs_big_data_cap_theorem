package test.milk.client

import io.ktor.http.*
import io.ktor.server.testing.*
import io.milk.httpclient.module
import org.junit.Test
import kotlin.test.assertEquals


class AppTest {
    @Test
    fun testIndex() = testApp {
        handleRequest(HttpMethod.Get, "/").apply {
            assertEquals(200, response.status()?.value)
        }
    }

    private fun testApp(callback: TestApplicationEngine.() -> Unit) {
        withTestApplication({ module() }) { callback() }
    }
}
