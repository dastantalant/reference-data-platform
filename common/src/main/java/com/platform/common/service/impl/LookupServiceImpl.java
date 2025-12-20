package com.platform.common.service.impl;

import com.platform.common.entity.Definition;
import com.platform.common.entity.ReferenceItem;
import com.platform.common.entity.base.Translation;
import com.platform.common.mapper.ReferenceItemMapper;
import com.platform.common.model.lookup.DictionaryLookupResponse;
import com.platform.common.model.lookup.ItemLookupResponse;
import com.platform.common.model.lookup.LookupBatchRequest;
import com.platform.common.repository.DefinitionRepository;
import com.platform.common.repository.ReferenceItemRepository;
import com.platform.common.service.LookupService;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import jakarta.persistence.EntityNotFoundException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LookupServiceImpl implements LookupService {

    private final ReferenceItemRepository itemRepository;
    private final DefinitionRepository definitionRepository;
    private final ReferenceItemMapper mapper;

    @Override
    @Cacheable(value = "dictionaries", key = "#code + '-' + #lang + '-' + #date", unless = "#result == null")
    public DictionaryLookupResponse getDictionary(String code, String lang, Instant date) {
        Definition definition = definitionRepository.findCurrentByCode(code)
                .orElseThrow(() -> new EntityNotFoundException("Dictionary not found: " + code));

        Instant actualDate = (date != null) ? date : Instant.now();
        List<ReferenceItem> items = itemRepository.findActiveItems(code, actualDate);

        return DictionaryLookupResponse.builder()
                .code(code)
                .i18n(mapTranslations(definition.getTranslations(), lang))
                .content(items.stream()
                        .map(item -> mapper.toLookupResponse(item, lang))
                        .toList())
                .build();
    }

    @Override
    public ItemLookupResponse getItem(String code, String key, Instant date) {
        Instant actualDate = (date != null) ? date : Instant.now();

        ReferenceItem item = itemRepository.findByCodeAndKey(code, key)
                .filter(i -> i.isValidOn(actualDate))
                .orElseThrow(() -> new EntityNotFoundException("Item not found or inactive: " + key));

        return mapper.toLookupResponse(item, null);
    }

    @Override
    public List<DictionaryLookupResponse> getBatch(LookupBatchRequest request) {
        List<DictionaryLookupResponse> response = new ArrayList<>();
        for (LookupBatchRequest.SingleRequest req : request.getRequests()) {
            try {
                response.add(getDictionary(req.getCode(), null, null));
            } catch (Exception _) {
            }
        }
        return response;
    }

    private Map<String, String> mapTranslations(List<Translation> translations, String lang) {
        if (translations == null) return Collections.emptyMap();
        return translations.stream()
                .filter(t -> lang == null || t.getLocale().equalsIgnoreCase(lang))
                .collect(Collectors.toMap(Translation::getLocale, Translation::getValue, (a, _) -> a));
    }
}