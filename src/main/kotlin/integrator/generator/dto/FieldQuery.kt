package integrator.generator.dto;

class FieldQuery(var name: String, var alias: String?) {

    override fun toString(): String {
        return "FieldQuery = { name: $name, alias: $alias }"
    }

}
