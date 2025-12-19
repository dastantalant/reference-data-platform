package com.platform.common.service.impl;

import com.platform.common.entity.Definition;
import com.platform.common.entity.ReferenceItem;
import com.platform.common.enums.Status;
import com.platform.common.exception.NotFoundException;
import com.platform.common.exception.ValidationException;
import com.platform.common.mapper.ReferenceItemMapper;
import com.platform.common.model.definition.PagedDefinitionResponse;
import com.platform.common.model.definition.RelationRule;
import com.platform.common.model.item.BatchResponse;
import com.platform.common.model.item.ItemUpsertRequest;
import com.platform.common.model.reference.ReferenceItemResponse;
import com.platform.common.model.reference.ReferenceUpsertRequest;
import com.platform.common.repository.DefinitionRepository;
import com.platform.common.repository.ReferenceItemRepository;
import com.platform.common.service.JsonValidationService;
import com.platform.common.service.ReferenceItemService;
import com.platform.common.util.JsonHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class ReferenceItemServiceImpl implements ReferenceItemService {

    private final ReferenceItemRepository repository;
    private final DefinitionRepository definitionRepository;
    private final JsonHelper jsonHelper;
    private final JsonValidationService jsonValidationService; // Новый валидатор
    private final ReferenceItemMapper mapper;


    @Override
    @Transactional(readOnly = true)
    public PagedDefinitionResponse<ReferenceItemResponse> findAll(String code, Pageable pageable, String q, Status status) {

        // 1. Строим спецификацию (фильтр)
        Specification<ReferenceItem> spec = buildSearchSpec(code, q, status);

        // 2. Выполняем запрос с пагинацией и фильтрацией
        Page<ReferenceItem> page = repository.findAll(spec, pageable);

        // 3. Маппим результат
        List<ReferenceItemResponse> content = page.getContent().stream()
                .map(mapper::toResponse)
                .toList();

        return PagedDefinitionResponse.<ReferenceItemResponse>builder()
                .content(content)
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .currentPage(page.getNumber())
                .build();
    }

    /**
     * Вспомогательный метод для построения SQL условия (WHERE ...)
     */
    private Specification<ReferenceItem> buildSearchSpec(String code, String q, Status status) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Обязательный фильтр по коду справочника
            predicates.add(cb.equal(root.get("code"), code));

            // 2. Фильтр по статусу (если передан)
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            // 3. Текстовый поиск (если передан q)
            // Ищем совпадение в Ключе (refKey) ИЛИ в Переводах (translations.value)
            if (q != null && !q.isBlank()) {
                String searchPattern = "%" + q.toLowerCase() + "%";

                // Предикат по ключу (refKey)
                Predicate keyLike = cb.like(cb.lower(root.get("key")), searchPattern);

                // Предикат по переводам (JOIN translations)
                // Используем LEFT JOIN, чтобы не отсечь записи без переводов (хотя таких быть не должно)
                var translationsJoin = root.join("translations", JoinType.LEFT);
                Predicate translationLike = cb.like(cb.lower(translationsJoin.get("value")), searchPattern);

                // Объединяем через OR
                predicates.add(cb.or(keyLike, translationLike));

                // Важно: так как мы сделали JOIN с коллекцией, могут появиться дубликаты родителя.
                // Добавляем DISTINCT.
                query.distinct(true);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    @Override
    @Transactional(readOnly = true)
    public ReferenceItemResponse findByKey(String code, String key) {
        ReferenceItem item = repository.findByCodeAndKey(code, key)
                .orElseThrow(() -> new NotFoundException("Item not found: " + key));
        return mapper.toResponse(item);
    }

    @Override
    public ReferenceItemResponse upsert(String code, String key, ItemUpsertRequest request) {
        // 1. Получаем определение (схему)
        Definition definition = definitionRepository.findCurrentByCode(code)
                .orElseThrow(() -> new NotFoundException("Definition not found: " + code));

        // 2. Подготовка сущности
        ReferenceItem item = repository.findByCodeAndKey(code, key)
                .orElse(ReferenceItem.builder()
                        .code(code)
                        .key(key)
                        .version(definition.getId().version())
                        .status(Status.DRAFT)
                        .build());

        // 3. Установка полей для валидации
        item.setValidFrom(request.getValidFrom());
        item.setValidTo(request.getValidTo());

        // 4. Валидация
        validateRequest(item, definition, request.getCommonContent());

        // 5. Сеттеры
        item.setContentLob(mapper.toJson(request.getCommonContent()));
        item.setTranslations(mapper.toEntityTranslations(request.getTranslations()));

        // 6. Первичное сохранение (чтобы сгенерировать ID для новых записей)
        item = repository.saveAndFlush(item);

        // 7. Обновление TreePath (теперь ID точно есть)
        boolean pathChanged = updateTreePath(item);

        // 8. Вторичное сохранение, если путь изменился
        if (pathChanged) {
            item = repository.save(item);
        }

        return mapper.toResponse(item);
    }

    @Override
    public BatchResponse batchUpsert(String code, List<ItemUpsertRequest> requests) {
        BatchResponse response = new BatchResponse();
        for (ItemUpsertRequest req : requests) {
            try {
                upsert(code, req.getRefKey(), req);
                response.incrementSuccess();
            } catch (Exception e) {
                response.addError(req.getRefKey(), e.getMessage());
            }
        }
        return response;
    }

    @Override
    public void delete(String code, String key) {
        ReferenceItem item = repository.findByCodeAndKey(code, key)
                .orElseThrow(() -> new NotFoundException("Item not found"));
        item.setStatus(Status.ARCHIVED);
        repository.save(item);
    }

    @Override
    public ReferenceItemResponse approve(String code, String key) {
        ReferenceItem item = repository.findByCodeAndKey(code, key)
                .orElseThrow(() -> new NotFoundException("Item not found"));
        item.setStatus(Status.ACTIVE);
        return mapper.toResponse(repository.save(item));
    }

    // -------------------------------------------------------------------------
    // VALIDATION LOGIC
    // -------------------------------------------------------------------------

    private void validateRequest(ReferenceItem item, Definition definition, Map<String, Object> contentMap) {
        // 1. JSON Schema (через JsonValidationService)
        jsonValidationService.validate(contentMap, definition.getSchemaLob());

        // 2. Cross-References
        validateCrossReferences(definition, contentMap);

        // 3. Time Overlaps
        validateOverlaps(item);
    }

    private void validateCrossReferences(Definition definition, Map<String, Object> content) {
        List<RelationRule> rules = jsonHelper.parseRules(definition.getValidationRulesLob());
        if (rules == null || rules.isEmpty()) return;

        for (RelationRule rule : rules) {
            Object valueObj = content.get(rule.getField());
            if (valueObj instanceof String valueToCheck && !valueToCheck.isBlank()) {
                boolean exists = repository.existsByCodeAndKeyAndStatus(
                        rule.getTarget(), valueToCheck, Status.ACTIVE
                );
                if (!exists) {
                    throw new ValidationException(String.format(
                            "Field '%s' references invalid key '%s' in '%s'",
                            rule.getField(), valueToCheck, rule.getTarget()));
                }
            }
        }
    }

    private void validateOverlaps(ReferenceItem item) {
        Instant from = item.getValidFrom() == null ? Instant.MIN : item.getValidFrom();
        Instant to = item.getValidTo() == null ? Instant.MAX : item.getValidTo();
        Long excludeId = item.getId() == null ? -1L : item.getId();

        boolean hasOverlap = repository.existsOverlap(
                item.getCode(), item.getKey(), from, to, excludeId
        );

        if (hasOverlap) {
            throw new ValidationException("Time overlap detected for key: " + item.getKey());
        }
    }

    private void resolveParent(ReferenceItem item, String parentKey) {
        if (parentKey != null && !parentKey.isBlank()) {
            ReferenceItem parent = repository.findByCodeAndKey(item.getCode(), parentKey)
                    .orElseThrow(() -> new NotFoundException("Parent not found: " + parentKey));

            if (item.getId() != null && item.getId().equals(parent.getId())) {
                throw new ValidationException("Item cannot be its own parent");
            }
            item.setParent(parent);
        } else {
            item.setParent(null);
        }
    }

    /**
     * Вычисляет и обновляет Materialized Path.
     * Формат: /root_id/parent_id/my_id/
     * @return true, если путь изменился
     */
    private boolean updateTreePath(ReferenceItem item) {
        String oldPath = item.getTreePath();
        String newPath;

        if (item.getParent() == null) {
            // Корневой элемент: /id/
            newPath = "/" + item.getId() + "/";
        } else {
            // Дочерний элемент: /parent_path/id/
            // Если у родителя вдруг нет пути (легаси данные), строим его лениво
            String parentPath = item.getParent().getTreePath();
            if (parentPath == null) {
                parentPath = "/" + item.getParent().getId() + "/";
            }
            newPath = parentPath + item.getId() + "/";
        }

        // Обновляем только если изменилось, чтобы не триггерить лишние update
        if (!newPath.equals(oldPath)) {
            item.setTreePath(newPath);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public void moveNode(String code, String itemKey, String newParentKey) {
        ReferenceItem item = repository.findByCodeAndKey(code, itemKey).orElseThrow();
        ReferenceItem newParent = repository.findByCodeAndKey(code, newParentKey).orElseThrow();

        String oldPath = item.getTreePath();
        String newPath = newParent.getTreePath() + item.getId() + "/";

        item.setParent(newParent);
        item.setTreePath(newPath);
        repository.save(item);

        List<ReferenceItem> descendants = repository.findByTreePathStartingWith(oldPath);
        for (ReferenceItem child : descendants) {
            String childSuffix = child.getTreePath().substring(oldPath.length());
            child.setTreePath(newPath + childSuffix);
        }
        repository.saveAll(descendants);
    }
}