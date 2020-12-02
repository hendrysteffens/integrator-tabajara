package integrator.generator.dto

enum class ValidationTemplate(val value: String, val import: String) {
        TRANSLATION_FIELD("@TranslationField(columnName = \"{{field}}\")\n", "import com.senior.hcm.integration.annotations.TranslationField;\n"),
        MIN("@Min(value = 1, message = \"O campo '{{field}}' deve ser maior ou igual a 1\")\n", ""),
        MAX("@Max(value = 999, message = \"O campo '{{field}}' deve ser menor ou igual a 999\")\n", ""),
        LOCAL_DATE_RAGE("@LocalDateRange(fieldName = \"{{field}}\")\n", "import com.senior.hcm.integration.annotations.LocalDateRange;\n"),
        NOT_NULL("@NotNull(message = \"O campo '{{field}}' não pode ser nulo\")\n", "import javax.validation.constraints.NotNull;\n"),
        NOT_EMPTY("@NotEmpty(message = \"O campo '{{field}}' não pode ser vazio\")\n", "import javax.validation.constraints.NotEmpty;\n"),
        SIZE("@Size(max = {{valueSize}}, message = \"O campo '{{field}}' não pode ter mais que 4 caracteres\")\n", "import javax.validation.constraints.Size;\n")
}