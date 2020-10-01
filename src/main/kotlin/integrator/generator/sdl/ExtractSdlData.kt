package integrator.generator.sdl;

class ExtractSdlData {

    enum class ValidationType {
        STRING_SIZE
    }
    data class Field(val name: String, val type: String, val validations :Map<ValidationType,String>?)


    companion object {
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

        private fun processFieldType(name: String, fieldType: String): Field {
            if (fieldType.contains("string")){
                return processFieldStringType(name, fieldType);
            }
            return Field(name, fieldType, null);
        }

        private fun processFieldStringType(name: String, fieldType: String): Field {
            var match = "(?<=\\(\\s).*(?=\\s\\))".toRegex().find(fieldType)

            if(match?.value != null){
                return  Field(name.trim(), fieldType.split(" ")[1].trim(), mapOf(ValidationType.STRING_SIZE to match?.value))
            }
            return  Field(name.trim(), fieldType.trim(), null)
        }

    }

}
