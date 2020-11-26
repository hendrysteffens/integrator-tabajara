package integrator.generator.util

import integrator.generator.App
import java.io.File
import java.io.FileInputStream
import java.util.*

class FileGenerator {

    fun createFileByNameAndText(fileName: String, text: String): java.io.File {
        val props = Properties()
        props.load(FileInputStream(App().getResouce()))

        var file = File(props.getProperty("integrator.localtion") + fileName)
        file.writeText(text)
        return file
    }


}