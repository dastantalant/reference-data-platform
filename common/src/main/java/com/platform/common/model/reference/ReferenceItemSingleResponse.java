package com.platform.common.model.reference;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.platform.common.model.definition.BaseDefinition;
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
public class ReferenceItemSingleResponse extends ReferenceItemResponse {

    private BaseDefinition definition;

}
