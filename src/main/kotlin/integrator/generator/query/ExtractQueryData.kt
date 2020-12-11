package integrator.generator.sdl;

import integrator.generator.App
import integrator.generator.dto.Field
import integrator.generator.dto.FieldQuery
import integrator.generator.dto.ValidationType
import java.io.File
import java.io.FileInputStream
import java.util.*
import kotlin.collections.ArrayList

object ExtractQueryData {
    fun extractDataQuery(entityName: String): ArrayList<FieldQuery> {
        val props = Properties()
        props.load(FileInputStream(App().getResouce()))
        val query = File(props.getProperty("integrator.location")).resolve("src/hcm-integration/queries/g5-$entityName-query.sql").bufferedReader().use { it.readText() }
        val queryFields = getFieldsOfQuery(query);

        return queryFields;
    }

    fun extractDataSyncQuery(entityName: String): ArrayList<FieldQuery> {
        val props = Properties()
        props.load(FileInputStream(App().getResouce()))
        val syncQuery = File(props.getProperty("integrator.location")).resolve("src/hcm-integration/queries/g5-sync-$entityName-query.sql").bufferedReader().use { it.readText() }
        val syncQueryFields = getFieldsOfQuery(syncQuery);

        return syncQueryFields;
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
