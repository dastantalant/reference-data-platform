package com.platform.common.repository;

import com.platform.common.entity.ReferenceItem;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReferenceItemRepositoryImpl implements ReferenceItemRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Override
    public List<ReferenceItem> findByJsonAttribute(String code, String jsonField, String value) {
        if (isPostgres()) {
            return searchPostgres(code, jsonField, value);
        } else {
            return searchGeneric(code, jsonField, value);
        }
    }

    private boolean isPostgres() {
        return driverClassName.contains("postgresql");
    }

    private List<ReferenceItem> searchPostgres(String code, String field, String value) {
        String sql = "SELECT * FROM reference_item WHERE code = :code " +
                "AND content_lob ->> :field = :value";

        return entityManager.createNativeQuery(sql, ReferenceItem.class)
                .setParameter("code", code)
                .setParameter("field", field)
                .setParameter("value", value)
                .getResultList();
    }

    private List<ReferenceItem> searchGeneric(String code, String field, String value) {
        String likePattern = "%\"" + field + "\": \"" + value + "\"%";

        return entityManager.createQuery(
                        "SELECT r FROM ReferenceItem r WHERE r.code = :code AND r.contentLob LIKE :pattern",
                        ReferenceItem.class)
                .setParameter("code", code)
                .setParameter("pattern", likePattern)
                .getResultList();
    }
}