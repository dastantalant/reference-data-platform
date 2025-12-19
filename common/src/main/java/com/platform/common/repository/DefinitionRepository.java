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

    // 1. Поиск по Коду или Названию (в переводах)
    // Используем DISTINCT, так как join с переводами может вернуть дубли
    // Обычно ищем только среди актуальных (isCurrent = true)
    @Query("SELECT DISTINCT d FROM Definition d " +
            "LEFT JOIN d.translations t " +
            "WHERE d.isCurrent = true " +
            "AND (" +
            "   LOWER(d.id.code) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "   OR LOWER(t.value) LIKE LOWER(CONCAT('%', :search, '%'))" +
            ")")
    Page<Definition> searchByCodeOrName(@Param("search") String search, Pageable pageable);

    // 2. Получить текущую активную версию справочника
    @Query("SELECT d FROM Definition d WHERE d.id.code = :code AND d.isCurrent = true")
    Optional<Definition> findCurrentByCode(@Param("code") String code);

    // 3. Проверка существования (хотя бы одной версии с таким кодом)
    @Query("SELECT COUNT(d) > 0 FROM Definition d WHERE d.id.code = :code")
    boolean existsByCode(@Param("code") String code);

    // 4. История версий (от новых к старым)
    // Обращаемся к полям внутри EmbeddedId через точку
    List<Definition> findAllByIdCodeOrderByIdVersionDesc(String code);

    // 5. Найти максимальный номер версии
    @Query("SELECT MAX(d.id.version) FROM Definition d WHERE d.id.code = :code")
    Optional<Integer> findMaxVersion(@Param("code") String code);
}
