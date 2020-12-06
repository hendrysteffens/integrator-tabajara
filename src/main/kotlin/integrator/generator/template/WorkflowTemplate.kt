package integrator.generator.template

import java.io.File

class WorkflowTemplate {
    val templateString: String
        get() {
            return File(this.javaClass.classLoader.getResource("Workflow.java.template").file).readText(Charsets.UTF_8)
        };


}

data class WorkflowReceipt(
        val entity: String
)// : DocumentData