package integrator.generator.sdl;

import integrator.generator.dto.Field
import integrator.generator.dto.ValidationType

object ExtractSdlData {
    fun extractData(entityText: String): Pair<String?, List<Field>> {
        var entitySplitted = entityText.split("\n")
        val entityName = "(?<=entity\\s).*(?=\\s\\{)".toRegex().find(entitySplitted.get(1))?.value
        var fields = ArrayList<Field>();
        entitySplitted
                .filterNot { s -> s.trim().startsWith("\"") || s.trim().startsWith("}") || s.contains("entity") }
                .forEach {
                    val fielLine = it.split(":");
                    fields.add(processFieldType(fielLine.get(0).trim(), fielLine.get(1)))
                }

        return Pair(entityName, fields)
    }

    private fun processFieldType(name: String, type: String): Field {
        var fieldType = type;
        if (fieldType.contains("string")) {
            return processFieldStringType(name, fieldType);
        }
        var isRequired = verifyRequiredField(fieldType)
        if(!fieldType.contains("string") &&
                !fieldType.contains("double") &&
                !fieldType.contains("date") &&
                !fieldType.contains("integer")) {
            fieldType = "string"
        }
        if (fieldType.contains("date")) fieldType = "LocalDate";
        return Field(name, fieldType.replace("?", ""), if (isRequired) mapOf(ValidationType.REQUIRED to null) else null);
    }

    private fun processFieldStringType(name: String, fieldType: String): Field {
        var match = "(?<=\\(\\s).*(?=\\s\\))".toRegex().find(fieldType)
        var isRequired = verifyRequiredField(fieldType)
        if (match?.value != null) {
            var validations:  MutableMap<ValidationType, String?> = mutableMapOf(ValidationType.STRING_SIZE to match?.value)
            if (isRequired)
                validations[ValidationType.REQUIRED] = null
            return Field(name.trim(), fieldType.split(" ")[1].trim().replace("?", ""), validations)
        }


        return Field(name.trim(), fieldType.trim(), if (isRequired) mapOf(ValidationType.REQUIRED to null) else null)
    }

    private fun verifyRequiredField(fieldType: String): Boolean {
        return !fieldType.contains("?")
    }
}
