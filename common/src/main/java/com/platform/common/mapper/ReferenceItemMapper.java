package com.platform.common.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.common.entity.ReferenceItem;
import com.platform.common.entity.base.Translation;
import com.platform.common.model.common.TranslationDto;
import com.platform.common.model.lookup.ItemLookupResponse;
import com.platform.common.model.reference.ReferenceItemResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ReferenceItemMapper {

    private final ObjectMapper objectMapper;

    // --- Admin API Mapping ---

    public ReferenceItemResponse toResponse(ReferenceItem item) {
        return ReferenceItemResponse.builder()
                .code(item.getCode())
                .refKey(item.getKey())
                .status(item.getStatus())
                .parentKey(item.getParent() != null ? item.getParent().getKey() : null)
                .validFrom(item.getValidFrom())
                .validTo(item.getValidTo())
                .commonContent(parseJson(item.getContentLob()))
                .translations(toDtos(item.getTranslations()))
                .createdBy(item.getCreatedBy())
                .createdAt(item.getCreatedAt())
                .updatedBy(item.getUpdatedBy())
                .updatedAt(item.getUpdatedAt())
                .build();
    }

    // --- Lookup API Mapping ---

    public ItemLookupResponse toLookupResponse(ReferenceItem item, String lang) {
        return ItemLookupResponse.builder()
                .refKey(item.getKey())
                .i18n(mapToI18n(item.getTranslations(), lang))
                .details(parseJson(item.getContentLob()))
                .build();
    }

    // --- Helpers ---

    public List<Translation> toEntityTranslations(List<TranslationDto> dtos) {
        if (dtos == null) return Collections.emptyList();
        return dtos.stream()
                .map(dto -> new Translation(dto.getLocale(), dto.getValue()))
                .collect(Collectors.toList());
    }

    private List<TranslationDto> toDtos(List<Translation> entities) {
        if (entities == null) return Collections.emptyList();
        return entities.stream()
                .map(t -> new TranslationDto(t.getLocale(), t.getValue()))
                .collect(Collectors.toList());
    }

    private Map<String, String> mapToI18n(List<Translation> translations, String lang) {
        if (translations == null) return Collections.emptyMap();
        return translations.stream()
                .filter(t -> lang == null || t.getLocale().equalsIgnoreCase(lang))
                .collect(Collectors.toMap(Translation::getLocale, Translation::getValue, (a, b) -> a));
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
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing JSON", e);
        }
    }
}