package integrator.generator.generator

import integrator.generator.dto.Field
import integrator.generator.sdl.ExtractQueryData
import integrator.generator.sdl.ExtractSdlData
import integrator.generator.tbs.TbsDataExtractor
import java.util.stream.Collectors

const val SPACE_PRIMARY_KEY: String = "                "
    const val STRING_BUILD: String = ".{{#fieldDto}}(payload.getAsString({{#fieldPayload}})) //"
fun generateWorkflow(extractData: TbsDataExtractor.G5TableDefinition, entityName: String, g5Table: String, templateString: String, fields: List<Field>): String {
    val queryData = ExtractQueryData.extractDataQuery(getEntityName(entityName, EntityNameType.TRACE))
    val syncQueryData = ExtractQueryData.extractDataSyncQuery(getEntityName(entityName, EntityNameType.TRACE))

    var workflow  = templateString.replace("{{#EntityTrace}}", getEntityName(entityName, EntityNameType.TRACE))
    var translatedEntityName = ""
    workflow = workflow.replace("{{#TableG5}}", g5Table)
    workflow  = workflow.replace("{{#EntityUnderline}}", getEntityName(entityName, EntityNameType.UNDERLINE_LOWER))
    workflow  = workflow.replace("{{#CollectionName}}", getEntityName(entityName, EntityNameType.UNDERLINE_UPPER))
    workflow  = workflow.replace("{{#EntityCamelCase}}", getEntityName(entityName, EntityNameType.CAMEL_CASE))
    workflow  = workflow.replace("{{#BodyBuild}}",  getBuild(fields))
    workflow  = workflow.replace("{{#BodyGetPrimaryKey}}", getPrimaryKeys(extractData))
    workflow  = workflow.replace("{{#ServiceName}}", ExtractSdlData.getServiceName().toUpperCase())
    workflow  = workflow.replace("{{#BodyMonitoredFields}}", getMonitoredFields(extractData))


    return workflow
}

fun getBuild(fields: List<Field>): String {
    return fields
            .stream()
            .filter{!(it.name.equals("id") || it.name.equals("externalId")|| it.name.equals("isIntegration"))}
            .map{ STRING_BUILD.replace("{{#fieldDto}}", getEntityName(it.name, EntityNameType.UNDERLINE_LOWER)) }
            .collect(Collectors.joining("\n"))
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