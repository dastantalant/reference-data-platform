package com.platform.common.model.reference;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.platform.common.model.base.AuditableDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReferenceItemResponse extends AuditableDto {

    @JsonProperty("is_valid")
    private boolean isValid;

    @JsonProperty("ref_key")
    private String refKey;

    private Map<String, Object> content;

}
