package com.platform.common.service;

import com.platform.common.model.definition.DefinitionCreateRequest;
import com.platform.common.model.definition.PagedDefinitionResponse;
import com.platform.common.model.definition.DefinitionResponse;
import com.platform.common.model.reference.ReferenceItemResponse;
import com.platform.common.model.system.ValidationReportResponse;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface DefinitionService {

    PagedDefinitionResponse<DefinitionResponse> findAll(Pageable pageable, String search);

    DefinitionResponse getByCode(String code);

    DefinitionResponse create(DefinitionCreateRequest request);

    List<DefinitionResponse> getVersions(String code);

    DefinitionResponse createVersion(String code, DefinitionCreateRequest request);

    DefinitionResponse publishVersion(String code, int version);

}
