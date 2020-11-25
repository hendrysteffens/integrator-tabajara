package integrator.generator.util

import com.beust.klaxon.Klaxon
import java.net.HttpURLConnection
import java.net.URL

class FileGenerator {

    fun createFileByNameAndText(fileName: String, text: String): java.io.File {
        val props = Properties()
        props.load(FileInputStream(App().getResouce()))

        var file = File(props.getProperty("integrator.localtion") + fileName)
        return file.writeText(text)
    }


}