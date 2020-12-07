package integrator.generator.util

import integrator.generator.App
import java.io.File
import java.io.FileInputStream
import java.util.*

class FileGenerator {

    fun createFileByNameAndText(fileName: String, text: String) {
        val props = Properties()
        props.load(FileInputStream(App().getResouce()))
        var folder = File(props.getProperty("integrator.location") + "src/hcm-integration/src/main/java/com/senior/hcm/integration/workflows")
        folder.mkdirs();
        var file = File(props.getProperty("integrator.location") + "src/hcm-integration/src/main/java/com/senior/hcm/integration/workflows/" + fileName.capitalize())
        file.writeText(text)
    }


}