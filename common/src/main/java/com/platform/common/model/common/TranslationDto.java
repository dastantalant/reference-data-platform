package com.platform.common.model.common;

import com.fasterxml.jackson.annotation.JsonView;
import com.platform.common.model.view.Views;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TranslationDto {

    @JsonView(Views.Summary.class)
    private String locale;

    @JsonView(Views.Summary.class)
    private String value;
}