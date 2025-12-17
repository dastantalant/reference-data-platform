package com.platform.common.repository;

import com.platform.common.entity.ReferenceItem;

import java.util.List;

public interface ReferenceItemRepositoryCustom {
    List<ReferenceItem> findByJsonAttribute(String code, String jsonField, String value);
}