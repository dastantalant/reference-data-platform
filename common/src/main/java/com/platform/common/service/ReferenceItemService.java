package com.platform.common.service;

import com.platform.common.model.reference.ReferenceUpsertRequest;
import com.platform.common.model.reference.ReferenceItemResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReferenceItemService {

    Page<ReferenceItemResponse> findAllByCode(String code, Pageable pageable);

    boolean validate(ReferenceUpsertRequest request);

    boolean exists(ReferenceUpsertRequest request);

    void delete(String code, String itemKey);
}
