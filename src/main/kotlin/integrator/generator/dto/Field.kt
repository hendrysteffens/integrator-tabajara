package integrator.generator.dto;

class Field(var name: String, var type: String, var validations: Map<ValidationType, String?>?) {

    val snakeCaseName: String
        get() = "(?<=[a-zA-Z])[A-Z]".toRegex().replace(name){
            "_${it.value}"
        }.toLowerCase()

    override fun toString(): String {
        return "Field = { name: $name, type: $type, validations: $validations }"
    }

}
