package integrator.generator.tbs

import integrator.generator.util.getRestApiResponse
import java.io.FileNotFoundException
import java.io.InputStream

fun downloadTbsFile(user: String, password: String, tableName: String, isEnum: Boolean = false): InputStream? {
    class AuthMetadata(val access_token: String)

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
            "https://git.senior.com.br/api/v4/projects/74/repository/files/br.com.senior.rh.tbs%2Fsrc%2Fbr%2Fcom%2Fsenior%2Frh%2F${if (isEnum) "enums" else "tables"}%2F${tableName}.rdbmf/raw?ref=master",
            "GET",
            token
        )
}