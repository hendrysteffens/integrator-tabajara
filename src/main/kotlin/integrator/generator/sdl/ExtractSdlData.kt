package integrator.generator.sdl;

class ExtractSdlData {

    enum class ValidationType {
        STRING_SIZE
    }
    data class Field(val name: String, val type: String, val validations :Map<ValidationType,String>)


    companion object {
        fun extractData(entityText: String): Pair<String?, List<Field>> {
            var entityLine = entityText.split("\n")
                    .get(1).split(" ")
            var size = entityLine.size
            println(entityLine.get(size - 2));
            println("(?<=entity\\s).*(?=\\s\\{)".toRegex().find(entityText)?.value)
            var match = "(?<=entity\\s).*(?=\\s\\{)".toRegex().find(entityText)
            match = null;
            println(match?.value)
            var entitySplitted = entityText.split("\n")
            val entityName = "(?<=entity\\s).*(?=\\s\\{)".toRegex().find(entitySplitted.get(1))?.value
            var fields = ArrayList<Field>();
            entitySplitted
                    .filterNot { s -> s.trim().startsWith("\"") || s.trim().startsWith("}") || s.contains("entity") }
                    .forEach {
                        val fielLine = it.split(":");
                        fields.add(Field(fielLine.get(0).trim(), processFieldType(fielLine.get(1).trim())))
                    }

            fields.forEach {
                println("Campo : " + it.name + " do tipo : " + it.type)
            }
            return Pair(entityName, fields)
        }

        private fun processFieldType(fieldType: String): String {
            if (fieldType.contains("string")){
                return processFieldStringType(fieldType);
            }
            return fieldType;
        }

        private fun processFieldStringType(fieldType: String): String {
            var match = "(?<=\\(\\s).*(?=\\s\\))".toRegex().find(fieldType)
            match?.value
        }

    }

}
