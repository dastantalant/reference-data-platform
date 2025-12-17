# Дамп пакета: com.platform.common.entity

- **Дата:** 2025-12-17T06:24:27.261705400
- **Корень проекта:** `C:\Users\dastan\home\github\reference-data-platform`
- **Файлов:** 7

---

### 📄 `common/src/main/java/com/platform/common/entity/base/BaseAudit.java`

```java
package com.platform.common.entity.base;

import com.platform.common.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
@MappedSuperclass
public abstract class BaseAudit {

    @CreatedBy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    @LastModifiedBy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private User updatedBy;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deleted_by")
    private User deletedBy;

    @Column(name = "deleted_at")
    private Instant deletedAt;

}
```

---

### 📄 `common/src/main/java/com/platform/common/entity/base/BaseEntity.java`

```java
package com.platform.common.entity.base;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.domain.Persistable;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
@MappedSuperclass
public abstract class BaseEntity<PK extends Serializable>
        extends BaseAudit
        implements Persistable<PK>, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private PK id;

    @Transient
    @Builder.Default
    private boolean isNew = true;

    @Override
    public PK getId() {
        return id;
    }

    @PostLoad
    @PostPersist
    @PostUpdate
    protected void markNotNew() {
        isNew = false;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy a
                        ? a.getHibernateLazyInitializer().getPersistentClass()
                        : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy a
                        ? a.getHibernateLazyInitializer().getPersistentClass()
                        : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        return getId() != null && Objects.equals(getId(), ((BaseEntity<?>) o).getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy a
                ? a.getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }
}
```

---

### 📄 `common/src/main/java/com/platform/common/entity/base/BaseIdentityEntity.java`

```java
package com.platform.common.entity.base;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
@MappedSuperclass
public abstract class BaseIdentityEntity extends BaseAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
```

---

### 📄 `common/src/main/java/com/platform/common/entity/base/Translate.java`

```java
package com.platform.common.entity.base;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Translate {

    @Column(name = "locale", nullable = false, length = 2)
    private String locale;

    @Column(name = "value", nullable = false, length = 1000)
    private String value;

}
```

---

### 📄 `common/src/main/java/com/platform/common/entity/Definition.java`

```java
package com.platform.common.entity;

import com.platform.common.entity.base.BaseAudit;
import com.platform.common.entity.base.Translation;
import com.platform.common.enums.Status;

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

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

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
    private boolean isCurrent = false;

    @Lob
    @Column(name = "schema_lob", nullable = false)
    private String schemaLob;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "definition_translation",
            joinColumns = {@JoinColumn(name = "definition_code", referencedColumnName = "code"),
                    @JoinColumn(name = "definition_version", referencedColumnName = "version")})
    @Builder.Default
    private List<Translate> translations = new ArrayList<>();

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
```

---

### 📄 `common/src/main/java/com/platform/common/entity/ReferenceItem.java`

```java
package com.platform.common.entity;

import com.platform.common.entity.base.BaseIdentityEntity;
import com.platform.common.entity.base.Translation;
import com.platform.common.enums.Status;

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

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "reference_item_translation", joinColumns = @JoinColumn(name = "item_id"))
    @Builder.Default
    private List<Translate> translations = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private ReferenceItem parent;

    @Column(name = "valid_from")
    private Instant validFrom;

    @Column(name = "valid_to")
    private Instant validTo;

    public boolean isValidOn(Instant date) {
        if (date == null) date = Instant.now();
        boolean startOk = (validFrom == null) || !date.isBefore(validFrom);
        boolean endOk = (validTo == null) || !date.isAfter(validTo);
        return startOk && endOk && status == Status.ACTIVE;
    }
}
```

---

### 📄 `common/src/main/java/com/platform/common/entity/User.java`

```java
package com.platform.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "password")
    private String password;

    @Builder.Default
    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;

    @Builder.Default
    @Column(name = "locked", nullable = false)
    private boolean locked = false;

    @CreationTimestamp
    private Instant createdAt;
}
```

---

