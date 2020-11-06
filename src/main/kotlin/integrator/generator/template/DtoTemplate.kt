package integrator.generator.template

import java.io.File

class DtoTemplate {
    val templateString: String
        get() {
            return File(this.javaClass.classLoader.getResource("Dto.java.template").file).readText(Charsets.UTF_8)
        };


}

data class DtoReceipt(
        val entity: String
)// : DocumentData