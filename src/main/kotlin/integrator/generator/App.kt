/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package integrator.generator

import integrator.generator.dto.Field
import integrator.generator.sdl.ExtractSdlData
import integrator.generator.template.DtoTemplate
import java.io.FileInputStream
import java.util.*

class App {
    val entity: String
        get() {
            return "\"Evento da folha de pagamento\"\n" +
                    "        custom public entity wagetype {\n" +
                    "            \"Id do evento\"\n" +
                    "            id : string ( 32 )\n" +
                    "            \"Relacionamento com tabelas de eventos\"\n" +
                    "            wageTypeTable : wageTypeTable\n" +
                    "            \"Código do evento\"\n" +
                    "            code : integer\n" +
                    "            \"Descrição do evento\"\n" +
                    "            name : string\n" +
                    "            \"Tipo de valor do evento\"\n" +
                    "            wageValueType : wageValueType\n" +
                    "            \"Tipo do evento\"\n" +
                    "            type : wageTypeCategory\n" +
                    "            \"Categoria do Evento\"\n" +
                    "            characteristic : wageTypeCharacteristic\n" +
                    "            \"Data de criação\"\n" +
                    "            creationdate : date?\n" +
                    "            \"Data de extinção\"\n" +
                    "            expirationdate : date?\n" +
                    "            \"Identificador do sindicato do da contribuição sindical\"\n" +
                    "            syndicate : string ( 32 )?\n" +
                    "        }"
        }

    fun getResouce() :String{
        return this.javaClass.classLoader.getResource("generator.properties").file;
    }

}

fun main(args: Array<String>) {
    val props = Properties()
    props.load(FileInputStream(App().getResouce()))
    props.getProperty("sdl.path")
    println(props.getProperty("sdl.path"))
    ExtractSdlData.extractData(App().entity).second.forEach {
        fields -> println(fields)
    }
    generateDto(ExtractSdlData.extractData(App().entity), DtoTemplate().templateString);

}

fun generateDto(extractData: Pair<String?, List<Field>>, templateString: String) {


}

