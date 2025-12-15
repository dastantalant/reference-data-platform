package com.platform.common.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.platform.common.model.definition.response.DefinitionResponse;
import com.platform.common.model.reference.response.ReferenceItemActiveResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response extends DefinitionResponse {
    private List<ReferenceItemActiveResponse> content;
}
