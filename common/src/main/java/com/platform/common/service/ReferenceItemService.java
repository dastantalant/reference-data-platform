package com.platform.common.service;

import com.platform.common.entity.Definition;
import com.platform.common.entity.ReferenceItem;
import com.platform.common.enums.Status;
import com.platform.common.model.definition.PagedDefinitionResponse;
import com.platform.common.model.item.BatchResponse;
import com.platform.common.model.item.ItemUpsertRequest;
import com.platform.common.model.reference.ReferenceUpsertRequest;
import com.platform.common.model.reference.ReferenceItemResponse;

import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface ReferenceItemService {

    PagedDefinitionResponse<ReferenceItemResponse> findAll(String code, Pageable pageable, String q, Status status);

    ReferenceItemResponse findByKey(String code, String key);

    ReferenceItemResponse upsert(String code, String key, ItemUpsertRequest request);

    BatchResponse batchUpsert(String code, List<ItemUpsertRequest> requests);

    ReferenceItemResponse approve(String code, String key);

    void moveNode(String code, String itemKey, String newParentKey);

    void delete(String code, String itemKey);

}
