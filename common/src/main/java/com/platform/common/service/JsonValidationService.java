package com.platform.common.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.Error;
import com.networknt.schema.InputFormat;
import com.networknt.schema.Schema;
import com.networknt.schema.SchemaRegistry;
import com.networknt.schema.SpecificationVersion;
import com.platform.common.exception.ValidationException;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class JsonValidationService {

    private final ObjectMapper objectMapper;
    private final SchemaRegistry schemaRegistry = SchemaRegistry.withDefaultDialect(SpecificationVersion.DRAFT_2020_12);

    public void validate(Map<String, Object> contentMap, String schemaJson) {
        if (schemaJson == null || schemaJson.isBlank()) {
            return;
        }

        try {
            String contentJson = objectMapper.writeValueAsString(contentMap);

            Schema schema = schemaRegistry.getSchema(schemaJson, InputFormat.JSON);

            List<Error> errors = schema.validate(contentJson, InputFormat.JSON, executionContext ->
                    executionContext.executionConfig(config -> config.formatAssertionsEnabled(true)));

            if (!errors.isEmpty()) {
                String errorMessage = errors.stream()
                        .map(this::formatErrorMessage)
                        .collect(Collectors.joining("; "));
                log.warn("JSON validation failed: {}", errorMessage);
                throw new ValidationException("Data validation error: " + errorMessage);
            }
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize content for validation", e);
            throw new ValidationException("Data processing error before validation", e);
        } catch (Exception e) {
            if (e instanceof ValidationException) throw e;
            log.error("Unexpected error during JSON validation", e);
            throw new RuntimeException("Internal schema validation error", e);
        }
    }

    private String formatErrorMessage(Error error) {
        return String.format("[%s] %s", error.getInstanceLocation(), error.getMessage());
    }

    public void validateMetaSchema(String schemaJson) {
        try {
            schemaRegistry.getSchema(schemaJson, InputFormat.JSON);
        } catch (Exception e) {
            throw new ValidationException("Invalid JSON Schema format: " + e.getMessage());
        }
    }
}