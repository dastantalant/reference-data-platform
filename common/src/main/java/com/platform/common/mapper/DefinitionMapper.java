package com.platform.common.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.common.entity.Definition;
import com.platform.common.entity.base.Translation;
import com.platform.common.model.common.TranslationDto;
import com.platform.common.model.definition.DefinitionResponse;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DefinitionMapper {

    private final ObjectMapper objectMapper;

    public DefinitionResponse toResponse(Definition entity) {
        return DefinitionResponse.builder()
                .code(entity.getId().code())
                .current(entity.isCurrent())
                .schema(parseJson(entity.getSchemaLob()))
                .translations(toDtos(entity.getTranslations()))
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public List<Translation> toEntityTranslations(List<TranslationDto> dtos) {
        if (dtos == null) return Collections.emptyList();
        return dtos.stream()
                .map(dto -> new Translation(dto.getLocale(), dto.getValue()))
                .collect(Collectors.toList());
    }

    public List<TranslationDto> toDtos(List<Translation> entities) {
        if (entities == null) return Collections.emptyList();
        return entities.stream()
                .map(t -> new TranslationDto(t.getLocale(), t.getValue()))
                .collect(Collectors.toList());
    }

    public String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing JSON", e);
        }
    }

    private Map<String, Object> parseJson(String json) {
        if (json == null || json.isEmpty()) return Collections.emptyMap();
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing JSON", e);
        }
    }
}