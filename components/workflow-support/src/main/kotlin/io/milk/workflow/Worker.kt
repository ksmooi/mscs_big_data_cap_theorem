package io.milk.workflow

interface Worker<T> {
    val name: String
    fun execute(task: T)
}
