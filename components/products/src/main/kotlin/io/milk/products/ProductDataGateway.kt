package io.milk.products

import io.milk.database.DatabaseTemplate
import io.milk.database.TransactionManager
import org.slf4j.LoggerFactory
import javax.sql.DataSource

class ProductDataGateway(private val dataSource: DataSource) {
    private val logger = LoggerFactory.getLogger(this.javaClass)
    private val template = DatabaseTemplate(dataSource)

    fun create(name: String, quantity: Int): ProductRecord {
        return template.create(
                "insert into products (name, quantity) values (?, ?)", { id ->
            ProductRecord(id, name, quantity)
        }, name, quantity
        )
    }

    fun findAll(): List<ProductRecord> {
        return template.findAll("select id, name, quantity from products order by id") { rs ->
            ProductRecord(rs.getLong(1), rs.getString(2), rs.getInt(3))
        }
    }

    fun findBy(id: Long): ProductRecord? {
        return template.findBy(
                "select id, name, quantity from products where id = ?", { rs ->
            ProductRecord(rs.getLong(1), rs.getString(2), rs.getInt(3))
        }, id
        )
    }

    fun update(product: ProductRecord): ProductRecord {
        template.update(
                "update products set name = ?, quantity = ? where id = ?",
                product.name, product.quantity, product.id
        )
        return product
    }

    fun decrementBy(purchase: PurchaseInfo) {
        return TransactionManager(dataSource).withTransaction {
            val found = template.findBy(
                    it,
                    "select id, name, quantity from products where id = ? for update", { rs ->
                ProductRecord(rs.getLong(1), rs.getString(2), rs.getInt(3))
            }, purchase.id
            )
            template.update(
                    it,
                    "update products set quantity = ? where id = ?",
                    (found!!.quantity - purchase.amount), purchase.id
            )
        }
    }

    fun fasterDecrementBy(purchase: PurchaseInfo) {
        logger.info(
                "decrementing the {} quantity by {} for product_id={}",
                purchase.name,
                purchase.amount,
                purchase.id
        )

        return TransactionManager(dataSource).withTransaction {
            template.update(
                    it,
                    "update products set quantity = (quantity - ?) where id = ?",
                    purchase.amount, purchase.id
            )
        }
    }
}