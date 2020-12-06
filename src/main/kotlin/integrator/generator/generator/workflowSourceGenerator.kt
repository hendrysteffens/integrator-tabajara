package integrator.generator.generator

import integrator.generator.tbs.TbsDataExtractor


fun generateWorkflow(extractData: TbsDataExtractor.G5TableDefinition, entityName: String, g5Table: String, templateString: String): String {

    var entityTrace = getEntityName(entityName, EntityNameType.TRACE);
    var translatedEntity = "";
    var tableG5 = g5Table;
    var entityUnderline = getEntityName(entityName, EntityNameType.UNDERLINE_LOWER);
    var collectionName = getEntityName(entityName, EntityNameType.UNDERLINE_UPPER);
    var entityCamelCase = getEntityName(entityName, EntityNameType.CAMEL_CASE);
    var bodyBuild = "";
    var bodyGetPrimaryKey = getPrimaryKeys(extractData);
    var g5QueryName = "";
    var g5SyncQueryName = "";
    var serviceName = "";
    var bodyMonitoredFields = "";


    return "";
}

fun getEntityName(name: String, type: EntityNameType): String {
    var entityName = "";
    var regex = Regex("(?<![0-9])(?=[A-Z])");

    entityName = when(type) {
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
        var key = removeSpaceAndSpecialCharacteres(it);

        if (extractData.fields[key]?.type.equals("DATE")) {
            payloadGetType += "payload.getAsLocalDate(\""+key+"\"), //\n                "
        } else {
            payloadGetType += "payload.getAsString(\""+key+"\"), //\n                \""
        }
    }

    return payloadGetType;
}

enum class EntityNameType {
    UNDERLINE_LOWER, CAMEL_CASE, TRACE, UNDERLINE_UPPER
}