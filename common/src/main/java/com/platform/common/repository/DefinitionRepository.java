package com.platform.common.repository;

import com.platform.common.entity.Definition;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DefinitionRepository extends JpaRepository<Definition, Definition.Id>, JpaSpecificationExecutor<Definition> {

    @Query("""
            SELECT DISTINCT d FROM Definition d
                LEFT JOIN d.translations t
            WHERE d.current = true
                AND (LOWER(d.id.code) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(t.value) LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<Definition> searchByCodeOrName(@Param("search") String search, Pageable pageable);

    @Query("SELECT d FROM Definition d WHERE d.id.code = :code AND d.current = true")
    Optional<Definition> findCurrentByCode(@Param("code") String code);

    @Query("SELECT COUNT(d) > 0 FROM Definition d WHERE d.id.code = :code")
    boolean existsByCode(@Param("code") String code);

    List<Definition> findAllByIdCodeOrderByIdVersionDesc(String code);

    @Query("SELECT MAX(d.id.version) FROM Definition d WHERE d.id.code = :code")
    Optional<Integer> findMaxVersion(@Param("code") String code);
}
