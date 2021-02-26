package integrator.generator.tbs

import integrator.generator.App
import java.io.BufferedReader
import java.io.InputStream
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

class TbsDataExtractor(private val props: Properties) {
    private val username: String = props.getProperty("gitlab.user")
    private val password: String = props.getProperty("gitlab.password")

    class G5Field(val name: String, val type: String, val required: Boolean)
    class G5ForeignKey(val tableName: String, val fields: Array<String>)
    class G5TableDefinition(
        val fields: Map<String, G5Field>,
        val primaryKey: Array<String>,
        val foreignKeys: Array<G5ForeignKey>?
    )

    fun extractTbsData(): G5TableDefinition? {
        val tableName: String = App.props.getProperty("integrator.g5.table")

        val g5FieldsInputStream = downloadTbsFile(username, password, tableName)

        if (g5FieldsInputStream != null) {
            val tbsContent = filterComments(g5FieldsInputStream)

            val tableOptions = """.*(?<=TABLE).*OPTIONS(.|\n)*?(?=]).*""".toRegex().find(tbsContent)
                    ?.value ?: "TABLE"

            val strColumns = tbsContent.substringAfter(tableOptions).split("COLUMN")

            val columns = strColumns.drop(1).map { strColumn ->
                createColumn(strColumn)
            }

            val constraints = strColumns.last().split("PRIMARY KEY").last()
            return generateTableDefinition(constraints, columns)
        }
        return null
    }

    private fun generateTableDefinition(
        constraints: String,
        columns: List<G5Field>
    ): G5TableDefinition {
        val fields = columns.map { it.name to it }.toMap()

        val foreignKeysRegex = """(?=(,)?FOREIGN\sKEY)((.|\n)*?)(?=(LOGICAL|(\s|.|\n)FOREIGN|INDEX))""".toRegex()

        val primaryKeyRegex = """((.|\n)*?)(?=(LOGICAL|(\s|.|\n)FOREIGN|INDEX))""".toRegex()
        val strPrimaryKey = primaryKeyRegex.find(constraints)?.value?.substringBefore(")")?.substringAfter("(")?: ""
        val primaryKey = strPrimaryKey.split(",").toTypedArray()

        val foreignKeys = foreignKeysRegex.findAll(constraints)
            .toList()
            .map { it.value }
            .map { G5ForeignKey(
                    it.substringAfter("REFERENCES ").substringBefore(" "),
                    it.substringAfter("(").substringBefore(")")
                        .split(",")
                        .toTypedArray()
                )
            }
            .toTypedArray()

        return G5TableDefinition(fields, primaryKey, foreignKeys)
    }

    private fun filterComments(inputStream: InputStream): String {
        val reader = BufferedReader(inputStream.reader())
        val sb = StringJoiner("\n")
        val comment = AtomicBoolean(false)
        reader.use {
            it.lines().forEach { _line ->
                var line = _line

                if (!comment.get()) {
                    if (line.trim().contains("/*")) {
                        line = line.substringBefore("/*")
                        comment.set(true)
                    }
                    sb.add(line)
                }
                if (comment.get() && line.trim().contains("*/")) {
                    line = line.substringAfter("*/")
                    comment.set(false)
                    if (line.isNotBlank()) {
                        sb.add(line)
                    }
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

        val type = getColumnType(strColumn)

        return G5Field(columnName, type, notNull)
    }

    private fun getColumnType(strColumn: String): String {
        val lines = strColumn.split("\n")
        val headerSplit = lines[0].split(" ")

        var type = headerSplit[2]

        if (lines.size > 1) {
            val memoryKind = lines[1].substringAfter("MEMORY KIND ", "")
            if (memoryKind.isNotEmpty()) {
                type = memoryKind
            }
        }

//        if (type == "DOMAIN" && level == "DEEP") {
//            val tableName = headerSplit[3].substringBefore("_")
//            val content = downloadTbsFile(username, password, tableName)
//            if (content != null) {
//                val filtered = filterComments(content)
//                val regex = """.*(?<=DOMAIN ${headerSplit[3]})(.|\n)*?(?=]).*"""
//                    .toRegex()
//                return getColumnType(regex.find(filtered)?.value
//                    ?: throw Exception("Não foi possível achar o tipo do campo ${headerSplit[1]} da tabela $tableName"), "FIRST")
//            } else {
//                throw Exception(
//                    """Erro ao ler conteúdo da linha ${lines[0]}
//    Conteúdo da tabela ${tableName} retornou vazio"""
//                )
//            }
//        }
        return type
    }

}

