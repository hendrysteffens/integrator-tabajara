package integrator.generator.generator

import integrator.generator.dto.Field
import integrator.generator.dto.ValidationTemplate
import integrator.generator.dto.ValidationType
import java.io.BufferedReader
import java.io.FileReader


const val SPACE_TAB: String = "    ";
public var listImport = arrayListOf<String>();
fun generateDto(extractData: Pair<String?, List<Field>>, templateString: String): String {
    var templateDto = convertEntityToDtoTemplate(extractData.first.toString(), templateString)
    var body = "";
    var importString = "";
    listImport = arrayListOf();

    extractData.second.forEach { field ->
        field.validations?.forEach {
            when (it.key) {
                ValidationType.REQUIRED -> {
                    body += getValidationStringWithField(ValidationTemplate.NOT_EMPTY.value, field.name)
                    body += getValidationStringWithField(ValidationTemplate.NOT_NULL.value, field.name);
                    importString += getImportValidation(ValidationTemplate.NOT_NULL);
                    importString += getImportValidation(ValidationTemplate.NOT_EMPTY);
                }
                ValidationType.MAX -> {
                    body += getValidationStringWithField(ValidationTemplate.MAX.value, field.name)
                    importString += getImportValidation(ValidationTemplate.MAX);
                }
                ValidationType.MIN -> {
                    body += getValidationStringWithField(ValidationTemplate.MIN.value, field.name)
                    importString += getImportValidation(ValidationTemplate.MIN);
                }
                ValidationType.DATE -> {
                    body += getValidationStringWithField(ValidationTemplate.LOCAL_DATE_RAGE.value, field.name)
                    importString += getImportValidation(ValidationTemplate.LOCAL_DATE_RAGE);
                }
            }
        }
        if (field.type.equals("LocalDate")) {
            field.type = "String";
        }
        var type = convertInitialLetter(field.type);
        body += SPACE_TAB + "private " + type + " " + field.name + "\n\n";
    }

    templateDto = templateDto.replace("/*{{Imports}}*/", importString);
    templateDto = templateDto.replace("{{BodyDtoTemplate}}", body);

    return templateDto;
}

fun convertEntityToDtoTemplate(entity: String, dtoFileString: String): String {
    var entityName = Regex("[^A-Za-z0-9]").replace(entity, "");

    return dtoFileString
            .replace("{{EntityNamePackage}}", entityName.toLowerCase())
            .replace("{{EntityName}}", convertInitialLetter(entityName))
}

fun getValidationStringWithField(rowValidator: String, field: String): String {
    return SPACE_TAB + rowValidator.replace("{{field}}", field)
}

fun getImportValidation(validation: ValidationTemplate): String {
    if (!listImport.contains(validation.name)) {
        listImport.add(validation.name)
        return ValidationTemplate.NOT_EMPTY.import
    }
    return ""
}

fun convertInitialLetter(text: String): String {
    return text.trim().substring(0, 1).toUpperCase().plus(text.trim().substring(1));
}