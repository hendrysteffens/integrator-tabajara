package integrator.generator.integrationlegacy

import integrator.generator.App
import integrator.generator.query.ExtractQueryData
import java.io.File
import kotlin.collections.ArrayList

class IntegrationLegacyExtractor {


    fun extractData(serviceName: String, entityName: String): List<Pair<String?, String?>> {
        val  mapperG5G7Fields = ArrayList<Pair<String?, String?>>();
        val mapperFile = File(App.props.getProperty("integrator.location"))
            .resolve("src/hcm-integration/src/main/java/com/senior/hcm/integration/mappers/${serviceName}/${entityName.capitalize()}Mapper.java")
        if (mapperFile.exists()) {
            val mapper = mapperFile.bufferedReader()
            val mapperString = mapper.use { it.readText() }
            ExtractQueryData.extractDataQuery(entityName)
                .stream()
                .forEach {
                    mapperG5G7Fields.add(
                        Pair(
                            it.alias?.trim(),
                            "(?<=\\.).*(?=\\=.*${it.alias?.trim()})".toRegex().find(mapperString)?.value?.trim()
                        )
                    )
                }
            return mapperG5G7Fields
        }
        return emptyList()
    }
}