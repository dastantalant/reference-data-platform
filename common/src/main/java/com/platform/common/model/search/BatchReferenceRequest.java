package com.platform.common.model.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class BatchReferenceRequest {
    private List<SingleRequest> requests;

    @Data
    public static class SingleRequest {
        private String dictionaryCode;
        private List<String> keys;
    }
}