package com.platform.common.model.lookup;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LookupBatchRequest {
    private List<SingleRequest> requests;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SingleRequest {
        private String code;
        private List<String> keys;
    }
}