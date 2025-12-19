package com.platform.common.service;

import com.platform.common.model.lookup.DictionaryLookupResponse;
import com.platform.common.model.lookup.ItemLookupResponse;
import com.platform.common.model.lookup.LookupBatchRequest;

import java.time.Instant;
import java.util.List;

public interface LookupService {
    DictionaryLookupResponse getDictionary(String code, String lang, Instant date);

    ItemLookupResponse getItem(String code, String key, Instant date);

    List<DictionaryLookupResponse> getBatch(LookupBatchRequest request);
}
