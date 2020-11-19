package integrator.generator.tbs

import integrator.generator.util.HttpUtils
import java.lang.Exception
import java.util.*

object TbsDataExtractor {
    class G5Field(val name: String, val type: String, val required: Boolean, val mask: String)
    class G5TableDefinition(val fields: Array<G5Field>, val key: Array<G5Field>)

    fun extractTbsData(props: Properties, tableName: String): G5TableDefinition? {
        // TODO
        //val g5Fields = getG5Fields(props, tableName)

        return null
    }

    private class AuthMetadata(val access_token: String)
    private class FileMetadata(val content: String)

    private fun getG5Fields(props: Properties, tableName: String): String {

        val user = props.getProperty("gitlab.user")
        val password = props.getProperty("gitlab.password")

        if (user.isBlank() || password.isBlank()) {
            throw Exception("Informar usuário e senha do gitlab no arquivo generator.properties.\n" +
                    "Ex.\n" +
                    "gitlab.user=USUÁRIO\n" +
                    "gitlab.passowrd=SENHA")
        }

        val token = HttpUtils.getRestApiResponse<AuthMetadata>(
            "https://git.senior.com.br/oauth/token?grant_type=password&username=${user}&password=${password}",
            "POST"
        ).access_token

        return HttpUtils.getRestApiResponse<FileMetadata>(
            "https://git.senior.com.br/api/v4/projects/74/repository/files/br.com.senior.rh.tbs%2Fsrc%2Fbr%2Fcom%2Fsenior%2Frh%2Ftables%2F${tableName}.rdbmf/raw?ref=master",
            "GET",
            token
        ).content
    }
}


