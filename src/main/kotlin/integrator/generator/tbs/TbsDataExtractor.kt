package integrator.generator.tbs

import integrator.generator.util.getRestApiResponse
import java.io.BufferedReader
import java.io.InputStream
import java.util.*

object TbsDataExtractor {
    class G5Field(val name: String, val type: String, val required: Boolean, val mask: String)
    class G5TableDefinition(val fields: Map<String, G5Field>, val primaryKey: Array<G5Field>)

    fun extractTbsData(props: Properties, tableName: String): G5TableDefinition? {
        val g5FieldsInputStream = getG5Fields(props, tableName)

        if (g5FieldsInputStream != null) {
            val reader = BufferedReader(g5FieldsInputStream.reader())

            reader.use {
                val contents = StringBuilder()

                do {
                    var line = reader.readLine()
                    var ignore = true
                    var column: String

                    if (line?.trim()?.startsWith("TABLE") == true) {
                        contents.append(line)
                    }

                    if (line?.trim()?.startsWith("COLUMN") == true) {
                        ignore = false
                        column = line.substringAfter("COLUMN").substringBefore(" ")
                        //createColumn(column, line)
                    }

                    if (!ignore) {

                        var comment = false

                        if (!comment) {
                            if (line?.trim()?.contains("/*") == true) {
                                line = line.substringBefore("/*")
                                comment = true
                            }
                            contents.append(line)
                        }
                        if (comment && line?.trim()?.contains("*/") == true) {
                            line = line.substringAfter("*/")
                            comment = false
                            if (line.isNotBlank()) {
                                contents.append(line)
                            }
                        }


                    }
                } while (line != null)

                print(contents.toString())
            }
        }
        return null
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

