package com.platform.common.model.definition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DefinitionCreateRequest {
    private String code;
    private Map<String, Object> schema;
}