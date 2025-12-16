package com.platform.common.model.definition;

import com.platform.common.model.common.TranslationDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DefinitionRequest {
    private String code;
    private Map<String, Object> schema;
    private List<TranslationDto> translations;
}