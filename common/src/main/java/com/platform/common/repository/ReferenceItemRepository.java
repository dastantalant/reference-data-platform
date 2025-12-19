package com.platform.common.repository;

import com.platform.common.entity.ReferenceItem;
import com.platform.common.enums.Status;
import com.platform.common.model.projection.I18nProjection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ReferenceItemRepository
        extends JpaRepository<ReferenceItem, Long>,
        JpaSpecificationExecutor<ReferenceItem>{


    /**
     * Используется для проверки существования записи (Cross-Reference Validation).
     * Важно проверять статус, чтобы не ссылаться на удаленные (ARCHIVED) или черновики (DRAFT).
     */
    // Для проверки Cross-Ref
    @Query("SELECT COUNT(r) > 0 FROM ReferenceItem r WHERE r.code = :code AND r.key = :key AND r.status = :status")
    boolean existsByCodeAndKeyAndStatus(@Param("code") String code, @Param("key") String key, @Param("status") Status status);
    // boolean existsByCodeAndKeyAndStatus(String code, String key, Status status);

    /**
     * Поиск родителя или конкретной записи для админки.
     * Возвращает Optional, чтобы можно было кинуть 404.
     */
    Optional<ReferenceItem> findByCodeAndKey(String code, String key);

    /**
     * Основной метод для получения справочника в публичном API.
     * 1. Фильтрует по коду.
     * 2. Берет только ACTIVE.
     * 3. Проверяет даты (validFrom <= now <= validTo).
     * 4. Использует LEFT JOIN FETCH для загрузки переводов (N+1 проблема),
     * так как translations теперь @ElementCollection.
     */
    @Query("""
            SELECT DISTINCT r FROM ReferenceItem r
            LEFT JOIN FETCH r.translations
            WHERE r.code = :code
                AND r.status = 'ACTIVE'
                AND (r.validFrom IS NULL OR r.validFrom <= :now)
                AND (r.validTo IS NULL OR r.validTo >= :now)
            """)
    List<ReferenceItem> findActiveItems(@Param("code") String code, @Param("now") Instant now);

    /**
     * Супер-быстрый метод, если нам не нужен contentLob (JSON), а только переводы.
     * Возвращает плоские данные, которые сервис соберет в Map.
     * Полезно для огромных справочников.
     */
    @Query("""
            SELECT r.key as refKey, t.locale as locale, t.value as value
            FROM ReferenceItem r
                JOIN r.translations t
            WHERE r.code = :code
                AND r.status = 'ACTIVE'
            """)
    List<I18nProjection> findTranslationsOnly(@Param("code") String code);

    @Query("""
            SELECT r FROM ReferenceItem r
                LEFT JOIN FETCH r.translations
            WHERE r.code = :code AND r.key = :key
            """)
    Optional<ReferenceItem> findByCodeAndKeyWithTranslations(@Param("code") String code, @Param("key") String key);

    @Query("""
            SELECT COUNT(r) > 0 FROM ReferenceItem r
            WHERE r.code = :code AND r.key = :key
                AND r.id != :excludeId
                AND ((r.validFrom < :newTo AND r.validTo > :newFrom))
            """)
    boolean existsOverlap(@Param("code") String code,
                          @Param("key") String key,
                          @Param("newFrom") Instant newFrom,
                          @Param("newTo") Instant newTo,
                          @Param("excludeId") Long excludeId);

    /**
     * Генерирует SQL: SELECT * FROM reference_item WHERE tree_path LIKE 'prefix%'
     * Очень эффективно использует обычный B-Tree индекс.
     */
    List<ReferenceItem> findByTreePathStartingWith(String pathPrefix);

    // 1. Поиск всех записей по коду справочника (для админки с пагинацией)
    // Это Derived Query метод, Spring Data сам сгенерирует SQL
    List<ReferenceItem> findAllByCode(String code);

    Page<ReferenceItem> findAllByCode(String code, Pageable pageable);

    // Дополнительный метод: если нужно фильтровать еще и по статусу в списке
    Page<ReferenceItem> findAllByCodeAndStatus(String code, Status status, Pageable pageable);

}