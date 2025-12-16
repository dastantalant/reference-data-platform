package com.platform.common.model.search;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.platform.common.model.reference.ReferenceResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EnrichedReferenceResponse {

    private String code;
    private ReferenceResponse content;

    @JsonIgnore
    @Builder.Default
    private Map<String, Object> attributes = new HashMap<>();

    @JsonAnyGetter
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @JsonAnySetter
    public void addAttribute(String key, Object value) {
        attributes.put(key, value);
    }
}
