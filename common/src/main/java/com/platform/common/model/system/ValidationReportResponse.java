package com.platform.common.model.system;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ValidationReportResponse {

    private String code;

    @Builder.Default
    private Instant timestamp = Instant.now();

    @JsonProperty("total_items")
    private long totalItems;

    @JsonProperty("valid_items")
    private long validItems;

    @JsonProperty("invalid_items")
    private long invalidItems;

    @Builder.Default
    private List<ValidationErrorItem> errors = new ArrayList<>();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidationErrorItem {

        @JsonProperty("ref_key")
        private String refKey;

        private String message;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String field;
    }
}