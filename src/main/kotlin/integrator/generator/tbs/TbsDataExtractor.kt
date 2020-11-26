package integrator.generator.tbs

import integrator.generator.util.getRestApiResponse
import java.io.BufferedReader
import java.io.InputStream
import java.util.*

object TbsDataExtractor {
    class G5Field(val name: String, val type: String, val required: Boolean, var description: String?)
    class G5TableDefinition(val fields: Map<String, G5Field>, val primaryKey: Array<G5Field>, var foreignKeys: Array<Array<G5Field>>?)

    fun extractTbsData(props: Properties, tableName: String): G5TableDefinition? {
        val g5FieldsInputStream = getG5Fields(props, tableName)

        if (g5FieldsInputStream != null) {
            val reader = BufferedReader(g5FieldsInputStream.reader())

            reader.use {
                val contents = StringBuilder()

                do {
                    var line = reader.readLine()
                    line = parseLine(line, contents)
                } while (line != null)

            }
        }
        return null
    }

    private fun parseLine(lineRaw: String?, contents: StringBuilder): String? {
        if (lineRaw == null) {
            return null
        }
        var line = lineRaw
        var ignore = true
        var columnName: String
        val fields = mutableListOf<G5Field>()

        if (line.trim().startsWith("TABLE")) {
            contents.append(line)
        }

        if (line.trim().startsWith("COLUMN")) {
            ignore = false
            columnName = line.substringAfter("COLUMN").trim().substringBefore(" ")
            fields.add(createColumn(columnName, line))
        }

        if (!ignore) {

            var comment = false

            if (!comment) {
                if (line.trim().contains("/*")) {
                    line = line.substringBefore("/*")
                    comment = true
                }
                contents.append(line)
            }
            if (comment && line.trim().contains("*/")) {
                line = line.substringAfter("*/")
                comment = false
                if (line.isNotBlank()) {
                    contents.append(line)
                }
            }


        }
        return line
    }

    private fun createColumn(columnName: String, line: String?)/*: G5Field*/ {
        var notNull = line?.contains("NOT NULL") == true
        var type: String
        if (line?.contains("DOMAIN") == true) {
            // TODO verificar de onde vem essa chave
            type = "String"
        }
//        var field = G5Field(columnName, )
//        return field
    }

    private fun getG5Fields(props: Properties, tableName: String): InputStream? {
        class AuthMetadata(val access_token: String)

        val user = props.getProperty("gitlab.user")
        val password = props.getProperty("gitlab.password")

        if (user.isBlank() || password.isBlank()) {
            throw Exception("Informar usuário e senha do gitlab no arquivo generator.properties.\n" +
                    "Ex.\n" +
                    "gitlab.user=USUÁRIO\n" +
                    "gitlab.passowrd=SENHA")
        }

        val token = getRestApiResponse<AuthMetadata>(
                "https://git.senior.com.br/oauth/token?grant_type=password&username=${user}&password=${password}",
                "POST"
        ).access_token

        return getRestApiResponse(
                "https://git.senior.com.br/api/v4/projects/74/repository/files/br.com.senior.rh.tbs%2Fsrc%2Fbr%2Fcom%2Fsenior%2Frh%2Ftables%2F${tableName}.rdbmf/raw?ref=master",
                "GET",
                token
        )
    }

}

