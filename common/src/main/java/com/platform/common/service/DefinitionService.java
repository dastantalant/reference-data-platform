package com.platform.common.service;

import com.platform.common.model.definition.DefinitionCreateRequest;
import com.platform.common.model.definition.PagedDefinitionResponse;
import com.platform.common.model.definition.DefinitionResponse;
import com.platform.common.model.reference.ReferenceItemActiveResponse;
import com.platform.common.model.reference.ReferenceItemResponse;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface DefinitionService {

    DefinitionResponse create(String code);

    PagedDefinitionResponse<ReferenceItemResponse> findAll(Pageable pageable);

    PagedDefinitionResponse<ReferenceItemActiveResponse> findAllByStatusEqualActive(Pageable pageable);

    DefinitionResponse update(DefinitionCreateRequest request);

    boolean validate(Map<String, Object> schema);

    boolean exists(String code);

    void delete(String code);
}
