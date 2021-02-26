package integrator.generator.sdl;

import integrator.generator.App
import integrator.generator.dto.Field
import integrator.generator.dto.ValidationType
import java.io.File
import java.io.FileInputStream
import java.util.*
import kotlin.collections.ArrayList

class ExtractSdlData(val props: Properties) {
    fun extractData(entityText: String): Pair<String?, List<Field>> {
        val entitySplitted = entityText.split("\n")
        val entityName = "(?<=entity\\s).*(?=\\s\\{)".toRegex().find(entitySplitted.get(1))?.value
        val fields = ArrayList<Field>();
        entitySplitted
            .filterNot { s -> s.trim().startsWith("\"") || s.trim().startsWith("}") || s.contains("entity") }
            .forEach {
                val fielLine = it.split(":")
                if (fielLine.size == 2)
                    fields.add(processFieldType(fielLine.get(0).trim(), fielLine.get(1)))
            }

        return Pair(entityName, fields)
    }

    private fun processFieldType(name: String, type: String): Field {
        var fieldType = type;
        if (fieldType.contains("string")) {
            return processFieldStringType(name, fieldType);
        }
        val isRequired = verifyRequiredField(fieldType)
        if (!fieldType.contains("string") &&
            !fieldType.contains("double") &&
            !fieldType.contains("date") &&
            !fieldType.contains("integer")
        ) {
            fieldType = "string"
        }
        if (fieldType.contains("date")) fieldType = "LocalDate";
        return Field(
            name,
            fieldType.replace("?", ""),
            if (isRequired) mapOf(ValidationType.REQUIRED to null) else null
        );
    }

    private fun processFieldStringType(name: String, fieldType: String): Field {
        val match = "(?<=\\(\\s).*(?=\\s\\))".toRegex().find(fieldType)
        val isRequired = verifyRequiredField(fieldType)
        if (match?.value != null) {
            val validations: MutableMap<ValidationType, String?> =
                mutableMapOf(ValidationType.STRING_SIZE to match?.value)
            if (isRequired)
                validations[ValidationType.REQUIRED] = null
            return Field(name.trim(), fieldType.split(" ")[1].trim().replace("?", ""), validations)
        }


        return Field(name.trim(), fieldType.trim(), if (isRequired) mapOf(ValidationType.REQUIRED to null) else null)
    }

    private fun verifyRequiredField(fieldType: String): Boolean {
        return !fieldType.contains("?")
    }

    fun getSdlEntityText(): String {
        val sdl = File(props.getProperty("integrator.backend.location")).resolve("main.sdl").bufferedReader()
        val sdlString = sdl.use { it.readText() }
        val regex = ".*\\n.*\\b(?<=entity " + props.getProperty("integrator.entity") + ")"
        val found = regex.toRegex().find(sdlString)?.range?.first
        return found?.let { sdlString.substring(it).substringBefore("}") }?: ""
    }

    fun getServiceName(): String {
        val sdl = File(props.getProperty("integrator.backend.location")).resolve("main.sdl").bufferedReader()
        val sdlString = sdl.use { it.readText() }
        val regex = "(?<=service\\s).*(?=\\s\\()"
        return regex.toRegex().find(sdlString)?.value ?: "";
    }

}
