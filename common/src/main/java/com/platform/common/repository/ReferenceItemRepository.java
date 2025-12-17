package com.platform.common.repository;

import com.platform.common.entity.ReferenceItem;
import com.platform.common.enums.Status;
import com.platform.common.model.projection.I18nProjection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ReferenceItemRepository extends JpaRepository<ReferenceItem, Long>, JpaSpecificationExecutor<ReferenceItem> {

    /**
     * Используется для проверки существования записи (Cross-Reference Validation).
     * Важно проверять статус, чтобы не ссылаться на удаленные (ARCHIVED) или черновики (DRAFT).
     */
    boolean existsByCodeAndKeyAndStatus(String code, String key, Status status);

    // -------------------------------------------------------------------------
    // 2. Admin API (Редактирование)
    // -------------------------------------------------------------------------

    /**
     * Поиск родителя или конкретной записи для админки.
     * Возвращает Optional, чтобы можно было кинуть 404.
     */
    Optional<ReferenceItem> findByCodeAndKey(String code, String key);

    // -------------------------------------------------------------------------
    // 3. Lookup API (Публичный доступ + Темпоральность)
    // -------------------------------------------------------------------------

    /**
     * Основной метод для получения справочника в публичном API.
     * 1. Фильтрует по коду.
     * 2. Берет только ACTIVE.
     * 3. Проверяет даты (validFrom <= now <= validTo).
     * 4. Использует LEFT JOIN FETCH для загрузки переводов (N+1 проблема),
     *    так как translations теперь @ElementCollection.
     */
    @Query("SELECT DISTINCT r FROM ReferenceItem r " +
            "LEFT JOIN FETCH r.translations " +
            "WHERE r.code = :code " +
            "AND r.status = 'ACTIVE' " +
            "AND (r.validFrom IS NULL OR r.validFrom <= :now) " +
            "AND (r.validTo IS NULL OR r.validTo >= :now)")
    List<ReferenceItem> findActiveItems(@Param("code") String code, @Param("now") Instant now);

    // -------------------------------------------------------------------------
    // 4. Оптимизация (Projection) - Если нужны только i18n
    // -------------------------------------------------------------------------

    /**
     * Супер-быстрый метод, если нам не нужен contentLob (JSON), а только переводы.
     * Возвращает плоские данные, которые сервис соберет в Map.
     * Полезно для огромных справочников.
     */
    @Query("SELECT r.key as refKey, t.locale as locale, t.value as value " +
            "FROM ReferenceItem r " +
            "JOIN r.translations t " +
            "WHERE r.code = :code " +
            "AND r.status = 'ACTIVE'")
    List<I18nProjection> findTranslationsOnly(@Param("code") String code);

}