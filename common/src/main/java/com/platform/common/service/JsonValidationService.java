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

    /**
     * Валидирует Map<String, Object> (контент) по JSON-схеме (строка).
     *
     * @param contentMap Данные для проверки (обычно приходят из Request Body)
     * @param schemaJson JSON Schema в виде строки (из БД)
     * @throws ValidationException если есть ошибки валидации
     */
    public void validate(Map<String, Object> contentMap, String schemaJson) {
        if (schemaJson == null || schemaJson.isBlank()) {
            return; // Если схемы нет, считаем, что валидация пройдена (или выбросить ошибку)
        }

        try {
            // 1. Преобразуем Map в JSON строку для валидатора
            // (Библиотека умеет работать и с JsonNode, но InputFormat.JSON надежнее для сырых данных)
            String contentJson = objectMapper.writeValueAsString(contentMap);

            // 2. Создаем объект Schema из строки
            Schema schema = schemaRegistry.getSchema(schemaJson, InputFormat.JSON);

            // 3. Запускаем валидацию
            List<Error> errors = schema.validate(contentJson, InputFormat.JSON, executionContext -> {
                // Включаем валидацию форматов (date, email, uri...), которая по умолчанию выключена в 2020-12
                executionContext.executionConfig(config -> config.formatAssertionsEnabled(true));
            });

            // 4. Если есть ошибки — собираем их и кидаем исключение
            if (!errors.isEmpty()) {
                String errorMessage = errors.stream()
                        .map(this::formatErrorMessage)
                        .collect(Collectors.joining("; "));

                log.warn("JSON validation failed: {}", errorMessage);
                throw new ValidationException("Ошибка валидации данных: " + errorMessage);
            }

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize content for validation", e);
            throw new ValidationException("Ошибка обработки данных перед валидацией", e);
        } catch (Exception e) {
            if (e instanceof ValidationException) throw e;
            log.error("Unexpected error during JSON validation", e);
            throw new RuntimeException("Внутренняя ошибка валидации схемы", e);
        }
    }

    /**
     * Форматирует сообщение об ошибке для клиента.
     */
    private String formatErrorMessage(Error error) {
        // Error содержит много деталей (schemaPath, instanceLocation).
        // Берем самое понятное сообщение.
        return String.format("[%s] %s", error.getInstanceLocation(), error.getMessage());
    }

    /**
     * Метод для валидации самой схемы (проверка, что админ загрузил валидный JSON Schema).
     * Полезно вызывать при создании Definition.
     */
    public void validateMetaSchema(String schemaJson) {
        try {
            // Пытаемся распарсить как схему
            schemaRegistry.getSchema(schemaJson, InputFormat.JSON);
        } catch (Exception e) {
            throw new ValidationException("Некорректный формат JSON Schema: " + e.getMessage());
        }
    }
}