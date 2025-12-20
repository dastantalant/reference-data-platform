package com.platform.common.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.common.entity.Definition;
import com.platform.common.entity.ReferenceItem;
import com.platform.common.model.system.ValidationReportResponse;
import com.platform.common.repository.DefinitionRepository;
import com.platform.common.repository.ReferenceItemRepository;
import com.platform.common.service.JsonValidationService;
import com.platform.common.service.SystemService;

import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class SystemServiceImpl implements SystemService {

    private final CacheManager cacheManager;
    private final ReferenceItemRepository itemRepository;
    private final DefinitionRepository definitionRepository;
    private final JsonValidationService jsonValidationService;
    private final ObjectMapper objectMapper;

    @Override
    public void evictCache(String code) {
        if (cacheManager.getCache("dictionaries") != null) {
            Objects.requireNonNull(cacheManager.getCache("dictionaries")).clear();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ValidationReportResponse validateDictionary(String code) {
        String schemaLob = definitionRepository.findCurrentByCode(code)
                .map(Definition::getSchemaLob)
                .orElse(null);

        if (schemaLob == null) {
            return ValidationReportResponse.builder()
                    .code(code)
                    .totalItems(0)
                    .build();
        }

        List<ReferenceItem> items = itemRepository.findAllByCode(code);
        List<ValidationReportResponse.ValidationErrorItem> errors = new ArrayList<>();

        for (ReferenceItem item : items) {
            try {
                Map<String, Object> contentMap = parseContent(item.getContentLob());
                jsonValidationService.validate(contentMap, schemaLob);
            } catch (Exception e) {
                errors.add(ValidationReportResponse.ValidationErrorItem.builder()
                        .refKey(item.getKey())
                        .message(e.getMessage())
                        .build());
            }
        }

        return ValidationReportResponse.builder()
                .code(code)
                .totalItems(items.size())
                .invalidItems(errors.size())
                .validItems(items.size() - errors.size())
                .errors(errors)
                .build();
    }

    private Map<String, Object> parseContent(String json) {
        if (json == null || json.isEmpty()) {
            return Collections.emptyMap();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Corrupted JSON data in DB: " + e.getMessage());
        }
    }

    @Override
    public byte[] export(String code) {
        return new byte[0];
    }
}