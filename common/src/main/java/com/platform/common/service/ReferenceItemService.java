package com.platform.common.service;

import com.platform.common.entity.Definition;
import com.platform.common.entity.ReferenceItem;
import com.platform.common.model.reference.ReferenceUpsertRequest;
import com.platform.common.model.reference.ReferenceItemResponse;

import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface ReferenceItemService {

    Page<ReferenceItemResponse> findAllByCode(String code, Pageable pageable);

    boolean validate(ReferenceUpsertRequest request);

    boolean exists(ReferenceUpsertRequest request);

    void delete(String code, String itemKey);

}
