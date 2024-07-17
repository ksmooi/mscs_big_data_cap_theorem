package test.milk

import io.milk.database.DatabaseTemplate
import io.milk.testsupport.testDataSource

class TestScenarioSupport {
    fun loadTestScenario(name: String) {
        val dataSource = testDataSource()
        val template = DatabaseTemplate(dataSource)
        this.javaClass.classLoader.getResourceAsStream("scenarios/$name.sql").reader().readLines()
            .asSequence()
            .filterNot(String::isNullOrBlank)
            .forEach {
                template.execute(it)
            }
    }
}