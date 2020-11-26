package integrator.generator.generator

import integrator.generator.dto.Field
import integrator.generator.dto.ValidationType
import integrator.generator.dto.ValidationTypeTemplate
import integrator.generator.getValidationStringWithField

fun generateDto(extractData: Pair<String?, List<Field>>, templateString: String): String {
    var bodyTemplate = "";
    extractData.second.forEach { field ->
        field.validations?.forEach {
            when(it.key) {
                ValidationType.REQUIRED -> {
                    bodyTemplate += getValidationStringWithField(ValidationTypeTemplate.NOT_EMPTY.value, field.name)
                    bodyTemplate += getValidationStringWithField(ValidationTypeTemplate.NOT_NULL.value, field.name);
                }
                ValidationType.MAX -> {
                    bodyTemplate += getValidationStringWithField(ValidationTypeTemplate.MAX.value, field.name)
                }
                ValidationType.MIN -> {
                    bodyTemplate += getValidationStringWithField(ValidationTypeTemplate.MIN.value, field.name)
                }
                ValidationType.DATE -> {
                    bodyTemplate += getValidationStringWithField(ValidationTypeTemplate.LOCAL_DATE_RAGE.value, field.name)
                }
            }
        }
        if(field.type.equals("LocalDate")){
            field.type = "String";
        }
        var type = field.type.trim().substring(0,1).toUpperCase().plus(field.type.trim().substring(1));

        bodyTemplate += "private "+type+" "+field.name+"\n\n";
    }
    return bodyTemplate;
}