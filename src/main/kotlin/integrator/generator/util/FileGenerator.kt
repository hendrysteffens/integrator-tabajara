package integrator.generator.util

import com.beust.klaxon.Klaxon
import integrator.generator.App
import java.io.File
import java.io.FileInputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class FileGenerator {

    fun createFileByNameAndText(fileName: String, text: String) {
        val props = Properties()
        props.load(FileInputStream(App().getResouce()))

        var file = File(props.getProperty("integrator.localtion") + fileName)
        return file.writeText(text)
    }


}