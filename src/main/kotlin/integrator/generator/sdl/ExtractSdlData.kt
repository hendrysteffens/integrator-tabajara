package integrator.generator.sdl;

class ExtractSdlData {
    companion object {
        fun extractData(entityText: String): Pair<String?, HashMap<String, String>> {
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
            var fields = HashMap<String, String>();
            entitySplitted
                    .filterNot { s -> s.trim().startsWith("\"") || s.trim().startsWith("}") || s.contains("entity") }
                    .forEach {
                        val fielLine = it.split(":");
                        fields.put(fielLine.get(0).trim(), fielLine.get(1).trim())
                    }

            fields.forEach { t: String, u: String ->
                println("Campo : " + t + " do tipo : " + u)
            }
            return Pair(entityName, fields)
        }

    }

}
