package com.platform.common.model.item;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BatchResponse {
    private int successCount = 0;
    private int failureCount = 0;
    private List<ErrorItem> errors = new ArrayList<>();

    public void incrementSuccess() {
        successCount++;
    }

    public void addError(String key, String msg) {
        failureCount++;
        errors.add(new ErrorItem(key, msg));
    }

    @Data
    @lombok.AllArgsConstructor
    public static class ErrorItem {
        private String refKey;
        private String message;
    }
}