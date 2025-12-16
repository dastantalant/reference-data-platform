package com.platform.common.model.item;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.platform.common.model.common.TranslationDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemUpsertRequest {

    @JsonProperty("ref_key")
    private String refKey;

    @JsonProperty("common_content")
    private Map<String, Object> commonContent;

    private List<TranslationDto> translations;
}