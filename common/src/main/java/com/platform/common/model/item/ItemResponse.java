package com.platform.common.model.item;

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
public class ItemResponse extends AuditableDto {

    @JsonView(Views.Summary.class)
    private String code;

    @JsonView(Views.Summary.class)
    @JsonProperty("ref_key")
    private String refKey;

    @JsonView(Views.Public.class)
    @JsonProperty("common_content")
    private Map<String, Object> commonContent;

    @JsonView(Views.Public.class)
    private List<TranslationDto> translations;
}