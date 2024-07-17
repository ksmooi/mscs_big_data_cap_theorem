package io.milk.database

import java.sql.*
import java.time.LocalDate
import javax.sql.DataSource

class DatabaseTemplate(private val dataSource: DataSource) {

    fun <T> create(sql: String, id: (Long) -> T, vararg params: Any) =
            dataSource.connection.use { connection ->
                create(connection, sql, id, *params)
            }

    fun <T> create(connection: Connection, sql: String, id: (Long) -> T, vararg params: Any): T {
        return connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS).use { statement ->
            setParameters(params, statement)
            statement.executeUpdate()
            val keys = statement.generatedKeys
            keys.next()
            id(keys.getLong(1))
        }
    }

    fun <T> findAll(sql: String, mapper: (ResultSet) -> T): List<T> {
        return query(sql, {}, mapper)
    }

    fun <T> findAll(connection: Connection, sql: String, mapper: (ResultSet) -> T): List<T> {
        return query(connection, sql, {}, mapper)
    }

    fun <T> findBy(sql: String, mapper: (ResultSet) -> T, id: Long): T? {
        dataSource.connection.use { connection ->
            return findBy(connection, sql, mapper, id)
        }
    }

    fun <T> findBy(connection: Connection, sql: String, mapper: (ResultSet) -> T, id: Long): T? {
        val list = query(connection, sql, { ps -> ps.setLong(1, id) }, mapper)
        return list.firstOrNull()
    }

    fun update(sql: String, vararg params: Any) {
        dataSource.connection.use { connection ->
            update(connection, sql, *params)
        }
    }

    fun update(connection: Connection, sql: String, vararg params: Any) {
        return connection.prepareStatement(sql).use { statement ->
            setParameters(params, statement)
            statement.executeUpdate()
        }
    }

    fun <T> query(sql: String, params: (PreparedStatement) -> Unit, mapper: (ResultSet) -> T): List<T> {
        dataSource.connection.use { connection ->
            return query(connection, sql, params, mapper)
        }
    }

    fun <T> query(
            connection: Connection,
            sql: String,
            params: (PreparedStatement) -> Unit,
            mapper: (ResultSet) -> T
    ): List<T> {
        val results = ArrayList<T>()
        connection.prepareStatement(sql).use { statement ->
            params(statement)
            statement.executeQuery().use { rs ->
                while (rs.next()) {
                    results.add(mapper(rs))
                }
            }
        }
        return results
    }

    private fun setParameters(params: Array<out Any>, statement: PreparedStatement) {
        for (i in params.indices) {
            val param = params[i]
            val parameterIndex = i + 1

            when (param) {
                is String -> statement.setString(parameterIndex, param)
                is Int -> statement.setInt(parameterIndex, param)
                is Long -> statement.setLong(parameterIndex, param)
                is Boolean -> statement.setBoolean(parameterIndex, param)
                is LocalDate -> statement.setDate(parameterIndex, Date.valueOf(param))

            }
        }
    }

    /// USED FOR TESTING

    fun execute(sql: String) {
        dataSource.connection.use { connection ->
            connection.prepareCall(sql).use(CallableStatement::execute)
        }
    }
}