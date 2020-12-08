package integrator.generator.tbs

import integrator.generator.App
import org.junit.Test

import java.io.FileInputStream
import java.util.*
import kotlin.test.assertNotNull

class TbsDataExtractorTest {

    @Test
    fun extractTbsData() {
        val props = Properties()
        props.load(FileInputStream(App().getResouce()))

        val g5table = "R044BAS"
        val result = TbsDataExtractor(props).extractTbsData(g5table)

        assertNotNull(result)
    }
}