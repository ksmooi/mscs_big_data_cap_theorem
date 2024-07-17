package io.milk.products

data class ProductInfo(val id: Long, val name: String, var quantity: Int) {
    fun incrementBy(amount: Int) {
        quantity += amount
    }

    fun decrementBy(amount: Int) {
        quantity -= amount
    }
}