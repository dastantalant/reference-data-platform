package com.platform.common.model.definition;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.platform.common.model.base.AuditableDto;
import com.platform.common.model.common.TranslationDto;
import com.platform.common.model.view.Views;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DefinitionResponse extends AuditableDto {

    @JsonView(Views.Summary.class)
    private String code;

    @JsonView(Views.Internal.class)
    @JsonProperty("is_current")
    private boolean isCurrent;

    @JsonView(Views.Public.class)
    private Map<String, Object> schema;

    @JsonView(Views.Public.class)
    private List<TranslationDto> translations;
}