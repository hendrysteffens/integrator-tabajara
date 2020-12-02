package com.senior.hcm.integration.workflows.wagetype;

import com.senior.hcm.integration.annotations.TranslationClass;
import com.senior.hcm.integration.annotations.TranslationField;
import com.senior.hcm.integration.workflows.builder.BaseFullWorkflowDto;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotEmpty;


@Builder
@Getter
public class WageTypeDto extends BaseFullWorkflowDto {

    @NotEmpty(message = "O campo 'id' não pode ser vazio")
    @NotNull(message = "O campo 'id' não pode ser nulo")
    private String id

    @NotEmpty(message = "O campo 'wageTypeTable' não pode ser vazio")
    @NotNull(message = "O campo 'wageTypeTable' não pode ser nulo")
    private String wageTypeTable

    @NotEmpty(message = "O campo 'code' não pode ser vazio")
    @NotNull(message = "O campo 'code' não pode ser nulo")
    private Integer code

    @NotEmpty(message = "O campo 'name' não pode ser vazio")
    @NotNull(message = "O campo 'name' não pode ser nulo")
    private String name

    @NotEmpty(message = "O campo 'wageValueType' não pode ser vazio")
    @NotNull(message = "O campo 'wageValueType' não pode ser nulo")
    private String wageValueType

    @NotEmpty(message = "O campo 'type' não pode ser vazio")
    @NotNull(message = "O campo 'type' não pode ser nulo")
    private String type

    @NotEmpty(message = "O campo 'characteristic' não pode ser vazio")
    @NotNull(message = "O campo 'characteristic' não pode ser nulo")
    private String characteristic

    private String creationdate

    private String expirationdate

    private String syndicate


}