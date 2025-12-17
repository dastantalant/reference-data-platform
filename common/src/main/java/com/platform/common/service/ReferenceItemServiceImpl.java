package com.platform.common.service;

import com.platform.common.entity.Definition;
import com.platform.common.entity.ReferenceItem;
import com.platform.common.enums.Status;
import com.platform.common.exception.NotFoundException;
import com.platform.common.exception.ValidationException;
import com.platform.common.model.definition.RelationRule;
import com.platform.common.model.reference.ReferenceItemResponse;
import com.platform.common.model.reference.ReferenceUpsertRequest;
import com.platform.common.repository.ReferenceItemRepository;
import com.platform.common.util.JsonHelper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import jakarta.transaction.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReferenceItemServiceImpl implements ReferenceItemService {

    private final ReferenceItemRepository repository;
    private final JsonHelper jsonHelper;

    @Override
    public Page<ReferenceItemResponse> findAllByCode(String code, Pageable pageable) {
        return null;
    }

    @Override
    public boolean validate(ReferenceUpsertRequest request) {
        return false;
    }

    @Override
    public boolean exists(ReferenceUpsertRequest request) {
        return false;
    }

    @Override
    public void delete(String code, String itemKey) {

    }

    /**
     * Валидация перекрестных ссылок (Cross-References).
     * Проверяет, существуют ли ключи в целевых справочниках и Активны ли они.
     */
    private void validateCrossReferences(Definition definition, Map<String, Object> content) {
        // Парсим правила валидации из JSON
        List<RelationRule> rules = jsonHelper.parseRules(definition.getValidationRulesLob());

        if (rules == null || rules.isEmpty()) return;

        for (RelationRule rule : rules) {
            // Получаем значение поля из пришедшего JSON (например, currency_code: "USD")
            Object valueObj = content.get(rule.getField());

            // Проверяем только если значение пришло и является строкой
            if (valueObj instanceof String valueToCheck && !valueToCheck.isBlank()) {

                // !!! ИСПОЛЬЗУЕМ НОВЫЙ МЕТОД РЕПОЗИТОРИЯ !!!
                // Важно проверять Status.ACTIVE, чтобы нельзя было сослаться на удаленную запись
                boolean exists = repository.existsByCodeAndKeyAndStatus(
                        rule.getTarget(), // Имя целевого справочника (напр. CURRENCY)
                        valueToCheck,               // Значение ключа (напр. USD)
                        Status.ACTIVE               // Требуем, чтобы запись была активна
                );

                if (!exists) {
                    throw new ValidationException(String.format(
                            "Поле '%s' ссылается на несуществующий или неактивный ключ '%s' в справочнике '%s'",
                            rule.getField(), valueToCheck, rule.getTarget()
                    ));
                }
            }
        }
    }

    /**
     * Установка родителя для иерархических справочников.
     * Преобразует parentKey (String) в parent (Entity).
     */
    private void resolveParent(ReferenceItem item, String parentKey) {
        if (parentKey != null && !parentKey.isBlank()) {

            // !!! ИСПОЛЬЗУЕМ НОВЫЙ МЕТОД РЕПОЗИТОРИЯ !!!
            // Ищем родителя в том же справочнике по ключу
            ReferenceItem parent = repository.findByCodeAndKey(item.getCode(), parentKey)
                    .orElseThrow(() -> new NotFoundException(String.format(
                            "Родительская запись с ключом '%s' не найдена в справочнике '%s'",
                            parentKey, item.getCode()
                    )));

            // Защита от цикла: запись не может быть родителем самой себя
            // (проверка работает только при обновлении, так как у новой записи еще нет ID)
            if (item.getId() != null && item.getId().equals(parent.getId())) {
                throw new ValidationException("Запись не может быть родителем самой себя");
            }

            item.setParent(parent);
        } else {
            // Если ключ не передан, сбрасываем родителя (актуально для update)
            item.setParent(null);
        }
    }

    @Transactional
    public void moveNode(String code, String itemKey, String newParentKey) {
        ReferenceItem item = repository.findByCodeAndKey(code, itemKey).orElseThrow();
        ReferenceItem newParent = repository.findByCodeAndKey(code, newParentKey).orElseThrow();

        String oldPath = item.getTreePath(); // Например: /1/10/50/

        // 1. Вычисляем новый путь для узла
        // Логика: Путь родителя + ID узла + /
        String newPath = newParent.getTreePath() + item.getId() + "/";

        // 2. Обновляем сам узел
        item.setParent(newParent);
        item.setTreePath(newPath);
        repository.save(item);

        // 3. Обновляем ВСЕХ потомков
        // Ищем всех, у кого путь начинался со старого пути
        List<ReferenceItem> descendants = repository.findByTreePathStartingWith(oldPath);

        for (ReferenceItem child : descendants) {
            // Заменяем префикс пути
            // Было: /1/10/50/99 -> Стало: /1/20/50/99
            String childSuffix = child.getTreePath().substring(oldPath.length());
            child.setTreePath(newPath + childSuffix);
        }

        repository.saveAll(descendants);
    }

    private void validateOverlaps(ReferenceItem item) {
        // Если to == null, считаем это "бесконечностью" (Instant.MAX)
        Instant to = item.getValidTo() == null ? Instant.MAX : item.getValidTo();
        Instant from = item.getValidFrom() == null ? Instant.MIN : item.getValidFrom();

        Long currentId = item.getId() == null ? -1L : item.getId();

        boolean hasOverlap = repository.existsOverlap(item.getCode(), item.getKey(), from, to, currentId);

        if (hasOverlap) {
            throw new ValidationException("Найдено пересечение дат с существующей записью");
        }
    }
}
