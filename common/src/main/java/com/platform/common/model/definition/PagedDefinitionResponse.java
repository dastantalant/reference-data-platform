package com.platform.common.model.definition;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class PagedDefinitionResponse<T> {
    private List<T> content;
    private long totalElements;
    private int totalPages;
    private int currentPage;

    private DefinitionResponse definition;
}