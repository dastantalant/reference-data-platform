package com.platform.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.common.model.definition.RelationRule;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class JsonHelper {

    private final ObjectMapper objectMapper;

    public List<RelationRule> parseRules(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<RelationRule>>() {
            });
        } catch (JsonProcessingException e) {
            log.error("Error parsing validation rules", e);
            throw new RuntimeException("Invalid validation rules format", e);
        }
    }

    public Map<String, Object> parseContent(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyMap();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {
            });
        } catch (JsonProcessingException e) {
            log.error("Error parsing content", e);
            throw new RuntimeException("Invalid content format", e);
        }
    }
}