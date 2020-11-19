package integrator.generator.dto

enum class ValidationTypeTemplate(val value: String) {
        TRANSLATION_FIELD("@TranslationField(columnName = \"{{field}}\")\n"),
        MIN("@Min(value = 1, message = \"O campo '{{field}}' deve ser maior ou igual a 1\")\n"),
        MAX("@Max(value = 999, message = \"O campo '{{field}}' deve ser menor ou igual a 999\")\n"),
        LOCAL_DATE_RAGE("@LocalDateRange(fieldName = \"{{field}}\")\n"),
        NOT_NULL("@NotNull(message = \"O campo '{{field}}' não pode ser nulo\")\n"),
        NOT_EMPTY("@NotEmpty(message = \"O campo '{{field}}' não pode ser vazio\")\n")
}