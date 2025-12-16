package com.platform.common.model.definition;

import com.fasterxml.jackson.annotation.JsonView;
import com.platform.common.model.view.Views;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagedDefinitionResponse<T> {

    @JsonView(Views.Summary.class)
    private List<T> content;

    @JsonView(Views.Summary.class)
    private long totalElements;

    @JsonView(Views.Summary.class)
    private int totalPages;

    @JsonView(Views.Summary.class)
    private int currentPage;

    @JsonView(Views.Public.class)
    private BaseDefinition definition;
}