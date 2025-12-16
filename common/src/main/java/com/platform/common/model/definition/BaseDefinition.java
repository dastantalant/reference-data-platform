package com.platform.common.model.definition;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.platform.common.model.base.AbstractBaseDto;
import com.platform.common.model.common.TranslationDto;
import com.platform.common.model.view.Views;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseDefinition extends AbstractBaseDto {

    @JsonView(Views.Summary.class)
    private String code;

    @JsonView(Views.Internal.class)
    @JsonProperty("is_current")
    private boolean isCurrent;

    @JsonView(Views.Summary.class)
    private List<TranslationDto> translations;
}