package com.platform.common.service;

import com.platform.common.model.reference.request.ReferenceRequest;
import com.platform.common.model.reference.response.ReferenceItemResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReferenceItemService {

    Page<ReferenceItemResponse> findAllByCode(String code, Pageable pageable);

    boolean validate(ReferenceRequest request);

    boolean exists(ReferenceRequest request);

    void delete(String code, String itemKey);
}
