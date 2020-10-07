package integrator.generator.dto;

class Field (name: String, type: String, validations: Map<ValidationType, String?>?){
    var name: String = name
        get() = field        // getter
        set(value) {         // setter
            field = value
        }
    var type: String = type
        get() = field        // getter
        set(value) {         // setter
            field = value
        }
    var validations: Map<ValidationType, String?>? = validations
        get() = field        // getter
        set(value) {         // setter
            field = value
        }

    override fun toString(): String {
        return "Field = { name: $name, type: $type, validations: $validations }"
    }

}
