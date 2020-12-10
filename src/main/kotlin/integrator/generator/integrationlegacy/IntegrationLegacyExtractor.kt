package integrator.generator.integrationlegacy

import integrator.generator.App
import integrator.generator.sdl.ExtractQueryData
import java.io.File
import java.io.FileInputStream
import java.util.*
import kotlin.collections.ArrayList

class IntegrationLegacyExtractor {


    fun extractData(serviceName: String, entityName: String): List<Pair<String?, String?>> {
        var  mapperG5G7Fields = ArrayList<Pair<String?, String?>>();
        val props = Properties()
        props.load(FileInputStream(App().getResouce()))
        val mapper = File(props.getProperty("integrator.location") + "src\\hcm-integration\\src\\main\\java\\com\\senior\\hcm\\integration\\mappers\\${serviceName}\\${entityName}Mapper").bufferedReader()
        val mapperString = mapper.use { it.readText() }
        ExtractQueryData.extractDataQuery(entityName)
                .stream()
                .forEach{
                    mapperG5G7Fields.add(Pair(it.alias,"(?<=\\.).*(?=\\=.*${it.name})".toRegex().find(mapperString)?.value))
                };
        return mapperG5G7Fields;
    }
}