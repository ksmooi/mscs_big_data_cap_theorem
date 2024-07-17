package io.milk.testsupport

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import javax.sql.DataSource

// These values should be configured according to your test database setup
val testDbPassword: String = "milk"
val testDbUsername: String = "milk"
val testJdbcUrl: String = "jdbc:postgresql://localhost:5432/milk_development"

// Function to create a data source for testing
fun testDataSource(): DataSource {
    val config = HikariConfig().apply {
        jdbcUrl = testJdbcUrl
        username = testDbUsername
        password = testDbPassword
        maximumPoolSize = 5
        minimumIdle = 2
        idleTimeout = 10000
        connectionTimeout = 30000
        maxLifetime = 1800000
    }
    return HikariDataSource(config)
}
