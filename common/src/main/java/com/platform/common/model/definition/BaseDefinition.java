package com.platform.common.model.definition;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.platform.common.model.base.AbstractBaseDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseDefinition extends AbstractBaseDto {

    private String code;

    @JsonProperty("is_current")
    private boolean isCurrent;
}
