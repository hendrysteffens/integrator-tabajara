package integrator.generator.util

import com.beust.klaxon.Klaxon
import java.net.HttpURLConnection
import java.net.URL

object HttpUtils {
    inline fun <reified T> getRestApiResponse(url: String, method: String, token: String = ""): T {
        with(URL(url).openConnection() as HttpURLConnection) {
            requestMethod = method

            if (token.isNotBlank()) {
                setRequestProperty("Authorization", "Bearer $token")
            }

            if (responseCode == 401) {
                throw Exception("Erro de autenticação com o gitlab, verifique suas configurações do generator.properties.\n" +
                        "Ex.\n" +
                        "gitlab.user=USUÁRIO\n" +
                        "gitlab.passowrd=SENHA")
            }
            return Klaxon().parse<T>(inputStream)!!
        }
    }
}