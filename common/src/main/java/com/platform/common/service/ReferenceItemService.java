package com.platform.common.service;

import com.platform.common.enums.Status;
import com.platform.common.model.definition.PagedDefinitionResponse;
import com.platform.common.model.item.BatchResponse;
import com.platform.common.model.item.ItemUpsertRequest;
import com.platform.common.model.reference.ReferenceItemResponse;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReferenceItemService {

    PagedDefinitionResponse<ReferenceItemResponse> findAll(String code, Pageable pageable, String q, Status status);

    ReferenceItemResponse findByKey(String code, String key);

    ReferenceItemResponse upsert(String code, String key, ItemUpsertRequest request);

    BatchResponse batchUpsert(String code, List<ItemUpsertRequest> requests);

    ReferenceItemResponse approve(String code, String key);

    void moveNode(String code, String itemKey, String newParentKey);

    void delete(String code, String itemKey);

}
