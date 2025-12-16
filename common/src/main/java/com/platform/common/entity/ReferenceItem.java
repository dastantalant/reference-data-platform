package com.platform.common.entity;

import com.platform.common.entity.base.BaseIdentityEntity;
import com.platform.common.enums.Status;
import jakarta.persistence.Column;
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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "reference_item", uniqueConstraints = @UniqueConstraint(name = "uq_reference_ref_key", columnNames = {"code", "version", "ref_key"}),
        indexes = @Index(name = "idx_reference_lookup", columnList = "code, ref_key"))
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
}
