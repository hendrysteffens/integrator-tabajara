package integrator.generator.query;

import integrator.generator.App
import integrator.generator.dto.FieldQuery
import java.io.File

object ExtractQueryData {
    fun extractDataQuery(entityName: String): List<FieldQuery> {
        val props = App.props
        val entityNameKebabCase = "(?<=[a-zA-Z])[A-Z]".toRegex().replace(entityName){
            "-${it.value}"
        }.toLowerCase()
        val queryFile = File(props.getProperty("integrator.location"))
            .resolve("src/hcm-integration/queries/g5-$entityNameKebabCase-query.sql")

        if (queryFile.exists()) {
            val query = queryFile
                .bufferedReader().use { it.readText() }

            val queryFields = getFieldsOfQuery(query);

            return queryFields;
        }
        return emptyList()
    }

    fun extractDataSyncQuery(entityName: String): List<FieldQuery> {
        val props = App.props
        val syncQueryFile = File(props.getProperty("integrator.location"))
            .resolve("src/hcm-integration/queries/g5-sync-$entityName-query.sql")
        if (syncQueryFile.exists()) {
            val syncQuery = syncQueryFile.bufferedReader().use { it.readText() }
            val syncQueryFields = getFieldsOfQuery(syncQuery);

            return syncQueryFields
        }
        return emptyList()
    }


    fun getFieldsOfQuery(query: String): ArrayList<FieldQuery> {
        val fields = ArrayList<FieldQuery>();
        val queryString = stringQueryProcessing(query)
        val list = queryString.split(",")

        list.forEach {

            if (it.contains("####")) {
                val name = it.substringBefore("####").substringAfter(".")
                val alias = it.substringAfter("####")

                fields.add(FieldQuery(name, alias));
            } else {
                fields.add(FieldQuery(it, null));
            }

        }

        return fields;
    }

    fun stringQueryProcessing(query: String): String {
        var queryString = query.toLowerCase()
                .substringAfter("select")
                .substringBefore("from")

        queryString = Regex("\\#([^\\>]+)\\#").replace(queryString, "");

        queryString = queryString
                .replace(" as ", "####")
                .replace(" ", "")
                .replace("\r\n", "")
                .replace("\t", "")

        return queryString;
    }

}
