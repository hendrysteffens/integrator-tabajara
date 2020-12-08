package integrator.generator.generator

import integrator.generator.sdl.ExtractSdlData
import integrator.generator.tbs.TbsDataExtractor

const val SPACE_PRIMARY_KEY: String = "                ";
    const val STRING_BUILD: String = ".{{#fieldDto}}(payload.getAsString({{#fieldPayload}})) //";
fun generateWorkflow(extractData: TbsDataExtractor.G5TableDefinition, entityName: String, g5Table: String, templateString: String): String {

    var entityTrace = getEntityName(entityName, EntityNameType.TRACE);
    var translatedEntityName = "";
    var tableG5 = g5Table;
    var entityUnderline = getEntityName(entityName, EntityNameType.UNDERLINE_LOWER);
    var collectionName = getEntityName(entityName, EntityNameType.UNDERLINE_UPPER);
    var entityCamelCase = getEntityName(entityName, EntityNameType.CAMEL_CASE);
    var bodyBuild = "";
    var bodyGetPrimaryKey = getPrimaryKeys(extractData);
    var serviceName = ExtractSdlData.getServiceName();
    var bodyMonitoredFields = getMonitoredFields(extractData);


    return "";
}

fun getEntityName(name: String, type: EntityNameType): String {
    var entityName = "";
    val regex = Regex("(?<![0-9])(?=[A-Z])");

    entityName = when (type) {
        EntityNameType.TRACE -> regex.replace(name, "-").toLowerCase();
        EntityNameType.UNDERLINE_LOWER -> regex.replace(name, "_").toLowerCase();
        EntityNameType.UNDERLINE_UPPER -> regex.replace(name, "_").toUpperCase();
        EntityNameType.CAMEL_CASE -> convertInitialLetter(name);
        else -> "Invalid Type"
    }

    return entityName;
}

fun getPrimaryKeys(extractData: TbsDataExtractor.G5TableDefinition): String {
    var payloadGetType = "                ";
    extractData.primaryKey.forEach {
        val key = removeSpaceAndSpecialCharacteres(it);

        if (extractData.fields[key]?.type.equals("DATE")) {
            payloadGetType += "payload.getAsLocalDate(\"" + key + "\"), //\n" + SPACE_PRIMARY_KEY
        } else {
            payloadGetType += "payload.getAsString(\"" + key + "\"), //\n" + SPACE_PRIMARY_KEY
        }
    }

    return payloadGetType;
}

fun getMonitoredFields(extractData: TbsDataExtractor.G5TableDefinition): String {
    var fields = "";
    var count = 0;
    extractData.primaryKey.forEach {
        val field = removeSpaceAndSpecialCharacteres(it);

        var comma = "";
        if (count != 0) comma = ", ";
        fields += comma + field.toLowerCase();
        count++;
    }

    return fields;
}

enum class EntityNameType {
    UNDERLINE_LOWER, CAMEL_CASE, TRACE, UNDERLINE_UPPER
}