package io.milk.workflow

import org.slf4j.LoggerFactory
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class WorkScheduler<T>(
    private val workFinder: WorkFinder<T>,
    private val workers: List<Worker<T>>,
    private val intervalSeconds: Long
) {
    private val logger = LoggerFactory.getLogger(WorkScheduler::class.java)
    private val scheduler = Executors.newScheduledThreadPool(workers.size)

    fun start() {
        scheduler.scheduleAtFixedRate({
            try {
                workers.forEach { worker ->
                    val tasks = workFinder.findRequested(worker.name)
                    tasks.forEach { task ->
                        worker.execute(task)
                        workFinder.markCompleted(task)
                    }
                }
            } catch (e: Exception) {
                logger.error("Error while scheduling work", e)
            }
        }, 0, intervalSeconds, TimeUnit.SECONDS)
    }

    fun stop() {
        scheduler.shutdown()
        try {
            if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                scheduler.shutdownNow()
            }
        } catch (e: InterruptedException) {
            scheduler.shutdownNow()
            Thread.currentThread().interrupt()
        }
    }
}
