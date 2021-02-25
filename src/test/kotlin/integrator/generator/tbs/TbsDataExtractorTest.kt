package integrator.generator.tbs

import integrator.generator.App
import org.junit.Test

import java.io.FileInputStream
import java.util.*
import kotlin.test.assertNotNull

class TbsDataExtractorTest {

    @Test
    fun extractTbsData() {
        val props = App.props

        val result = TbsDataExtractor(props).extractTbsData()

        assertNotNull(result)
    }
}