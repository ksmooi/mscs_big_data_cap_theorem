package io.milk.products

class ProductService(private val productDataGateway: ProductDataGateway) {

    fun findAll(): List<ProductRecord> {
        return productDataGateway.findAll()
    }

    fun findBy(id: Long): ProductRecord {
        return productDataGateway.findBy(id) ?: throw ProductNotFoundException("Product with id $id not found")
    }

    fun update(purchase: PurchaseInfo): ProductRecord {
        // Use decrementBy to ensure proper stock handling and avoid dirty reads
        productDataGateway.decrementBy(purchase)
        return findBy(purchase.id)
    }

    fun decrementBy(purchase: PurchaseInfo) {
        val product = findBy(purchase.id)
        if (product.quantity < purchase.amount) {
            throw InsufficientStockException("Insufficient stock for product with id ${purchase.id}")
        }
        product.quantity -= purchase.amount
        productDataGateway.update(product)
    }
}

class ProductNotFoundException(message: String) : RuntimeException(message)
class InsufficientStockException(message: String) : RuntimeException(message)
