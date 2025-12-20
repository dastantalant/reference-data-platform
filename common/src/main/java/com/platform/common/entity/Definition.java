package com.platform.common.entity;

import com.platform.common.entity.base.BaseAudit;
import com.platform.common.entity.base.Translation;
import com.platform.common.enums.Status;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "definition", indexes = @Index(name = "idx_definition_code", columnList = "code"))
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class Definition extends BaseAudit {

    @EmbeddedId
    private Id id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private Status status;

    @Builder.Default
    @Column(name = "is_current", nullable = false)
    private boolean current = false;

    @Lob
    @Column(name = "schema_lob", nullable = false)
    private String schemaLob;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "definition_translation",
            joinColumns = {@JoinColumn(name = "definition_code", referencedColumnName = "code"),
                    @JoinColumn(name = "definition_version", referencedColumnName = "version")})
    @Builder.Default
    private List<Translation> translations = new ArrayList<>();

    @Lob
    @Column(name = "validation_rules_lob")
    private String validationRulesLob;

    @Embeddable
    public record Id(
            @Column(name = "code", length = 100, nullable = false)
            String code,

            @Column(name = "version", nullable = false)
            int version
    ) {
    }
}
