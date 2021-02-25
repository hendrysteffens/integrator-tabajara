package integrator.generator.util

import integrator.generator.App
import java.io.File
import java.io.FileInputStream
import java.util.*

class FileGenerator {

    fun createFileByNameAndText(entityName :String, fileName: String, text: String) {
        val props = App.props
        var folder = File(props.getProperty("integrator.location") + "src/hcm-integration/src/main/java/com/senior/hcm/integration/workflows/${entityName}")
        folder.mkdirs();
        var file = File(props.getProperty("integrator.location") + "src/hcm-integration/src/main/java/com/senior/hcm/integration/workflows/${entityName}/${fileName.capitalize()}")
        file.writeText(text)
    }
}