package integrator.generator.tbs

import java.io.BufferedReader
import java.io.InputStream
import java.util.*

class TbsDataExtractor(private val props: Properties) {
    private val username: String = props.getProperty("gitlab.user")
    private val password: String = props.getProperty("gitlab.password")

    class G5Field(val name: String, val type: String, val required: Boolean, var description: String?)
    class G5TableDefinition(
        val fields: Map<String, G5Field>,
        val primaryKey: Array<G5Field>,
        var foreignKeys: Array<Array<G5Field>>?
    )

    fun extractTbsData(tableName: String): G5TableDefinition? {
        val g5FieldsInputStream = downloadTbsFile(username, password, tableName, true)

        if (g5FieldsInputStream != null) {
            val reader = BufferedReader(g5FieldsInputStream.reader())

            reader.use {
                val tbsContent = filterComments(reader)

                val columns = tbsContent.substringAfter("TABLE").split("COLUMN")

                columns.forEachIndexed { index, strColumn ->
                    if (index > 0) {
                        val column = createColumn(strColumn)
                    }
                }

            }
        }
        return null
    }

    private fun filterComments(reader: BufferedReader): String {
        val sb = StringBuilder()
        reader.lines().forEach { t ->
            var line = t
            var comment = false

            if (!comment) {
                if (line.trim().contains("/*")) {
                    line = line.substringBefore("/*")
                    comment = true
                }
                sb.append(line)
            }
            if (comment && line.trim().contains("*/")) {
                line = line.substringAfter("*/")
                comment = false
                if (line.isNotBlank()) {
                    sb.append(line)
                }
            }
        }
        return sb.toString()
    }


    private fun createColumn(splitLine: List<String>): G5Field {
        val columnName = splitLine[1]

        val indexOfNot = splitLine.indexOf("NOT")
        var notNull = indexOfNot >= 0 && splitLine.getOrNull(indexOfNot+1) == "NULL"

        var type = splitLine[2]
        if (type == "DOMAIN" || type == "ENUM") { // todo procurar por memory kind
            val originDomain = splitLine[3].substringBefore("_")
            val tbsContent = downloadTbsFile(username, password, originDomain, type == "ENUM")
                ?: throw Exception("Não foi possível baixar o conteúdo do tbs $originDomain")
            val superColumn = findColumn(tbsContent, columnName)
                ?: throw Exception("Não foi possível achar o campo $columnName na tabela $originDomain")
            type = superColumn.trim().split(" ")[2]
        }

    }

    private fun findColumn(tbsContent: InputStream, columnName: String): String? {
        val br = BufferedReader(tbsContent.reader())

        val pattern = "COLUMN\\s$columnName".toRegex()

        var line = br.readLine();
        while (line != null) {
            if (pattern.containsMatchIn(line)) {
                return line
            }
            line = br.readLine()
        }

        return null;
    }

    private fun getOriginDomain(line: String): String {

    }


}

