package com.platform.common.model.reference;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.platform.common.model.base.AuditableDto;
import com.platform.common.model.common.TranslationDto;
import com.platform.common.model.definition.DefinitionResponse;
import com.platform.common.model.view.Views;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReferenceItemResponse extends AuditableDto {

    @JsonView(Views.Summary.class)
    @JsonProperty("ref_key")
    private String refKey;

    @JsonView(Views.Summary.class)
    private String code;

    @JsonView(Views.Public.class)
    @JsonProperty("is_valid")
    private boolean isValid;

    @JsonView(Views.Public.class)
    @JsonProperty("common_content")
    private Map<String, Object> commonContent;

    @JsonView(Views.Public.class)
    private List<TranslationDto> translations;

    @JsonView(Views.Internal.class)
    private DefinitionResponse definition;

    private String parentKey;
    private Instant validFrom;
    private Instant validTo;

    private List<String> validationWarnings;
}