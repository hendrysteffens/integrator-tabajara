package integrator.generator.generator

import integrator.generator.App
import integrator.generator.dto.Field
import integrator.generator.integrationlegacy.IntegrationLegacyExtractor
import integrator.generator.query.ExtractQueryData
import integrator.generator.sdl.ExtractSdlData
import integrator.generator.tbs.TbsDataExtractor
import java.util.stream.Collectors

const val SPACE_PRIMARY_KEY: String = "                "
    const val STRING_BUILD: String = ".{{#fieldDto}}(payload.getAsString({{#fieldPayload}})) //"
fun generateWorkflow(extractData: TbsDataExtractor.G5TableDefinition, entityName: String, templateString: String, fields: List<Field>): String {
    val queryData = ExtractQueryData.extractDataQuery(getEntityName(entityName, EntityNameType.TRACE))
    val syncQueryData = ExtractQueryData.extractDataSyncQuery(getEntityName(entityName, EntityNameType.TRACE))

    val g5Table = App.props.getProperty("integrator.g5.table")

    var workflow  = templateString.replace("{{#EntityTrace}}", getEntityName(entityName, EntityNameType.TRACE))
    var translatedEntityName = ""
    workflow = workflow.replace("{{#TableG5}}", g5Table)
    workflow  = workflow.replace("{{#EntityUnderline}}", getEntityName(entityName, EntityNameType.UNDERLINE_LOWER))
    workflow  = workflow.replace("{{#CollectionName}}", getEntityName(entityName, EntityNameType.UNDERLINE_UPPER))
    workflow  = workflow.replace("{{#EntityCamelCase}}", getEntityName(entityName, EntityNameType.CAMEL_CASE))
    workflow  = workflow.replace("{{#BodyBuild}}",  getBuild(fields, ExtractSdlData(App.props).getServiceName(),entityName))
    workflow  = workflow.replace("{{#BodyGetPrimaryKey}}", getPrimaryKeys(extractData))
    workflow  = workflow.replace("{{#ServiceName}}", ExtractSdlData(App.props).getServiceName().toUpperCase())
    workflow  = workflow.replace("{{#BodyMonitoredFields}}", getMonitoredFields(extractData))


    return workflow
}

fun getBuild(fields: List<Field>, serviceName : String, entityName: String): String {
    val mapG5G7Fields = IntegrationLegacyExtractor().extractData(serviceName, entityName);
    return fields
            .stream()
            .filter{!(it.name.equals("id") || it.name.equals("externalId")|| it.name.equals("isIntegration"))}
            .map{ STRING_BUILD.replace("{{#fieldDto}}", getEntityName(it.name, EntityNameType.UNDERLINE_LOWER))
                    .replace("{{#fieldPayload}}", "\"${getFieldDto(mapG5G7Fields, it.snakeCaseName)}\"") }
            .collect(Collectors.joining("\n"))
}

fun getFieldDto(mapG5G7Fields : List<Pair<String?, String?>>, g7Field: String) : String {
    val mapG5G7FieldsFiltered = mapG5G7Fields.stream().filter{ it.first != null && it.second != null}.collect(Collectors.toList())
    return mapG5G7FieldsFiltered.stream().filter{ it.second == g7Field}.findFirst().orElse(null)?.first.orEmpty()
}

fun getEntityName(name: String, type: EntityNameType): String {
    var entityName = ""
    val regex = Regex("(?<![0-9])(?=[A-Z])")

    entityName = when (type) {
        EntityNameType.TRACE -> regex.replace(name, "-").toLowerCase()
        EntityNameType.UNDERLINE_LOWER -> regex.replace(name, "_").toLowerCase()
        EntityNameType.UNDERLINE_UPPER -> regex.replace(name, "_").toUpperCase()
        EntityNameType.CAMEL_CASE -> convertInitialLetter(name)
        else -> "Invalid Type"
    }

    return entityName
}

fun getPrimaryKeys(extractData: TbsDataExtractor.G5TableDefinition): String {
    var payloadGetType = ""
    extractData.primaryKey.forEach {
        val key = removeSpaceAndSpecialCharacteres(it)

        if (extractData.fields[key]?.type.equals("DATE")) {
            payloadGetType += "payload.getAsLocalDate(\"" + key + "\"), //\n" + SPACE_PRIMARY_KEY
        } else {
            payloadGetType += "payload.getAsString(\"" + key + "\"), //\n" + SPACE_PRIMARY_KEY
        }
    }

    return payloadGetType.substringBeforeLast(",")
}

fun getMonitoredFields(extractData: TbsDataExtractor.G5TableDefinition): String {
    var fields = ""
    var count = 0
    extractData.primaryKey.forEach {
        val field = removeSpaceAndSpecialCharacteres(it).toLowerCase()
        var comma = ""
        if (count != 0) comma = ", "
        fields += "$comma\"$field\""
        count++
    }

    return fields
}

enum class EntityNameType {
    UNDERLINE_LOWER, CAMEL_CASE, TRACE, UNDERLINE_UPPER
}