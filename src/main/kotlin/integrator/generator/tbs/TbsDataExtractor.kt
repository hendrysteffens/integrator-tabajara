package integrator.generator.tbs

import java.io.BufferedReader
import java.io.InputStream
import java.util.*

class TbsDataExtractor(private val props: Properties) {
    private val username: String = props.getProperty("gitlab.user")
    private val password: String = props.getProperty("gitlab.password")

    class G5Field(val name: String, val type: String, val required: Boolean)
    class G5ForeignKey(val tableName: String, val fields: Array<String>)
    class G5TableDefinition(
        val fields: Map<String, G5Field>,
        val primaryKey: Array<String>,
        var foreignKeys: Array<G5ForeignKey>?
    )

    fun extractTbsData(tableName: String): G5TableDefinition? {
        val g5FieldsInputStream = downloadTbsFile(username, password, tableName, true)

        if (g5FieldsInputStream != null) {
            val reader = BufferedReader(g5FieldsInputStream.reader())

            reader.use {
                val tbsContent = filterComments(reader)

                val strColumns = tbsContent.substringAfter("TABLE").split("COLUMN")

                val columns = strColumns.drop(1).map { strColumn ->
                    createColumn(strColumn)
                }

                val constraints = strColumns.last().split("PRIMARY KEY").last()
                return generateTableDefinition(constraints, columns)
            }
        }
        return null
    }

    private fun generateTableDefinition(
        constraints: String,
        columns: List<G5Field>
    ): G5TableDefinition {
        val fields = columns.map { it.name to it }.toMap()

        val splitConstraints = constraints.split("\n")

        val strPrimaryKey = splitConstraints.first().substringAfterLast("(")
            .substringBeforeLast(")")

        val primaryKey = strPrimaryKey.split(",").toTypedArray()

        val foreignKeys = splitConstraints.drop(1)
            .filter { it.contains("FOREIGN KEY") }
            .map { G5ForeignKey(
                    it.substringAfter("REFERENCES ").substringBefore(" "),
                    it.substringBefore("(").substringAfter(")")
                        .split(",")
                        .toTypedArray()
                )
            }
            .toTypedArray()

        return G5TableDefinition(fields, primaryKey, foreignKeys)
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


    private fun createColumn(strColumn: String): G5Field {
        val lines = strColumn.split("\n")
        val headerSplit = lines[0].split(" ")
        val columnName = headerSplit[1]

        val indexOfNot = headerSplit.indexOf("NOT")
        val notNull = indexOfNot >= 0 && headerSplit.getOrNull(indexOfNot+1) == "NULL"

        var type = headerSplit[2]
        val memoryKind = lines[1].substringAfter("MEMORY KIND ", "")
        if (memoryKind.isNotEmpty()) {
            type = memoryKind
        }

        return G5Field(columnName, type, notNull)
    }

}

