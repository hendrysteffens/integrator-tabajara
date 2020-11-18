package integrator.generator.dto;

class Field(var name: String, var type: String, var validations: Map<ValidationType, String?>?) {

    override fun toString(): String {
        return "Field = { name: $name, type: $type, validations: $validations }"
    }

}
