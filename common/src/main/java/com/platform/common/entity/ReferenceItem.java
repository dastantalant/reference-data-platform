package com.platform.common.entity;

import com.platform.common.entity.base.BaseIdentityEntity;
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
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reference_item",
        uniqueConstraints = @UniqueConstraint(name = "uq_reference_ref_key", columnNames = {"code", "version", "ref_key"}),
        indexes = {@Index(name = "idx_reference_lookup", columnList = "code, ref_key"), @Index(name = "idx_ref_status", columnList = "status")})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class ReferenceItem extends BaseIdentityEntity {

    @Column(name = "code", nullable = false, length = 100)
    private String code;

    @Builder.Default
    @Column(name = "version", nullable = false)
    private int version = 1;

    @Column(name = "ref_key", nullable = false, length = 100)
    private String key;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private Status status;

    @Lob
    @Column(name = "content_lob", nullable = false)
    private String contentLob;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({@JoinColumn(name = "code", referencedColumnName = "code", insertable = false, updatable = false),
            @JoinColumn(name = "version", referencedColumnName = "version", insertable = false, updatable = false)})
    private Definition definition;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "reference_item_translation", joinColumns = @JoinColumn(name = "item_id"))
    @Builder.Default
    private List<Translation> translations = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private ReferenceItem parent;

    @Column(name = "valid_from")
    private Instant validFrom;

    @Column(name = "valid_to")
    private Instant validTo;

    @Column(name = "tree_path", length = 1000)
    private String treePath;

    public boolean isValidOn(Instant date) {
        if (date == null) date = Instant.now();
        boolean startOk = (validFrom == null) || !date.isBefore(validFrom);
        boolean endOk = (validTo == null) || !date.isAfter(validTo);
        return startOk && endOk && status == Status.ACTIVE;
    }
}
