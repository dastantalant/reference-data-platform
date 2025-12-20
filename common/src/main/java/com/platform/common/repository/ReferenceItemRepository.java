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

public interface ReferenceItemRepository extends JpaRepository<ReferenceItem, Long>, JpaSpecificationExecutor<ReferenceItem> {


    @Query("SELECT COUNT(r) > 0 FROM ReferenceItem r WHERE r.code = :code AND r.key = :key AND r.status = :status")
    boolean existsByCodeAndKeyAndStatus(@Param("code") String code, @Param("key") String key, @Param("status") Status status);

    Optional<ReferenceItem> findByCodeAndKey(String code, String key);

    @Query("""
            SELECT DISTINCT r FROM ReferenceItem r
                LEFT JOIN FETCH r.translations
            WHERE r.code = :code
                AND r.status = 'ACTIVE'
                AND (r.validFrom IS NULL OR r.validFrom <= :now)
                AND (r.validTo IS NULL OR r.validTo >= :now)
            """)
    List<ReferenceItem> findActiveItems(@Param("code") String code, @Param("now") Instant now);

    @Query("""
            SELECT r.key as refKey, t.locale as locale, t.value as value
            FROM ReferenceItem r
                JOIN r.translations t
            WHERE r.code = :code AND r.status = 'ACTIVE'
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
            WHERE r.code = :code
                AND r.key = :key
                AND r.id != :excludeId
                AND ((r.validFrom < :newTo AND r.validTo > :newFrom))
            """)
    boolean existsOverlap(@Param("code") String code,
                          @Param("key") String key,
                          @Param("newFrom") Instant newFrom,
                          @Param("newTo") Instant newTo,
                          @Param("excludeId") Long excludeId);

    List<ReferenceItem> findByTreePathStartingWith(String pathPrefix);

    List<ReferenceItem> findAllByCode(String code);

    Page<ReferenceItem> findAllByCode(String code, Pageable pageable);

    Page<ReferenceItem> findAllByCodeAndStatus(String code, Status status, Pageable pageable);

}