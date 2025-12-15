package com.platform.common.service;

import com.platform.common.model.definition.request.DefinitionRequest;
import com.platform.common.model.definition.response.DefinitionPage;
import com.platform.common.model.definition.response.DefinitionResponse;
import com.platform.common.model.reference.response.ReferenceItemActiveResponse;
import com.platform.common.model.reference.response.ReferenceItemResponse;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface DefinitionService {

    DefinitionResponse create(String code);

    DefinitionPage<ReferenceItemResponse> findAll(Pageable pageable);

    DefinitionPage<ReferenceItemActiveResponse> findAllByStatusEqualActive(Pageable pageable);

    DefinitionResponse update(DefinitionRequest request);

    boolean validate(Map<String, Object> schema);

    boolean exists(String code);

    void delete(String code);
}
