# Дамп пакета: com.platform.common.model

- **Дата:** 2025-12-17T05:02:18.558555200
- **Корень проекта:** `C:\Users\dastan\home\github\reference-data-platform`
- **Файлов:** 13

---

### 📄 `common/src/main/java/com/platform/common/model/base/AbstractBaseDto.java`

```java
package com.platform.common.model.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.platform.common.enums.Status;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class AbstractBaseDto {

    private int version;

    private Status status;
}
```

---

### 📄 `common/src/main/java/com/platform/common/model/base/AuditableDto.java`

```java
package com.platform.common.model.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class AuditableDto extends AbstractBaseDto {
    @JsonProperty("created_by")
    private String createdBy;

    @JsonProperty("created_at")
    private Instant createdAt;

    @JsonProperty("updated_by")
    private String updatedBy;

    @JsonProperty("updated_at")
    private Instant updatedAt;

    @JsonProperty("deleted_by")
    private String deletedBy;

    @JsonProperty("deleted_at")
    private Instant deletedAt;
}
```

---

### 📄 `common/src/main/java/com/platform/common/model/definition/BaseDefinition.java`

```java
package com.platform.common.model.definition;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.platform.common.model.base.AbstractBaseDto;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseDefinition extends AbstractBaseDto {

    private String code;

    @JsonProperty("is_current")
    private boolean isCurrent;
}
```

---

### 📄 `common/src/main/java/com/platform/common/model/definition/DefinitionCreateRequest.java`

```java
package com.platform.common.model.definition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DefinitionCreateRequest {
    private String code;
    private Map<String, Object> schema;
}
```

---

### 📄 `common/src/main/java/com/platform/common/model/definition/DefinitionResponse.java`

```java
package com.platform.common.model.definition;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.platform.common.model.base.AuditableDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DefinitionResponse extends AuditableDto {

    private String code;

    @JsonProperty("is_current")
    private boolean isCurrent;

    private Map<String, Object> schema;
}
```

---

### 📄 `common/src/main/java/com/platform/common/model/definition/PagedDefinitionResponse.java`

```java
package com.platform.common.model.definition;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class PagedDefinitionResponse<T> {
    private List<T> content;
    private long totalElements;
    private int totalPages;
    private int currentPage;

    private DefinitionResponse definition;
}
```

---

### 📄 `common/src/main/java/com/platform/common/model/reference/ReferenceItemActiveResponse.java`

```java
package com.platform.common.model.reference;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.platform.common.enums.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReferenceItemActiveResponse extends ReferenceItemResponse {

    @JsonIgnore
    public Status getStatus() {
        return super.getStatus();
    }
}
```

---

### 📄 `common/src/main/java/com/platform/common/model/reference/ReferenceItemResponse.java`

```java
package com.platform.common.model.reference;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.platform.common.model.base.AuditableDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReferenceItemResponse extends AuditableDto {

    @JsonProperty("is_valid")
    private boolean isValid;

    @JsonProperty("ref_key")
    private String refKey;

    private Map<String, Object> content;

}
```

---

### 📄 `common/src/main/java/com/platform/common/model/reference/ReferenceItemSingleResponse.java`

```java
package com.platform.common.model.reference;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.platform.common.model.definition.BaseDefinition;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReferenceItemSingleResponse extends ReferenceItemResponse {

    private BaseDefinition definition;

}
```

---

### 📄 `common/src/main/java/com/platform/common/model/reference/ReferenceResponse.java`

```java
package com.platform.common.model.reference;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReferenceResponse {
    @JsonProperty("ref_key")
    private String refKey;

    @JsonIgnore
    @Builder.Default
    private Map<String, Object> attributes = new HashMap<>();

    @JsonAnyGetter
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @JsonAnySetter
    public void addAttribute(String key, Object value) {
        attributes.put(key, value);
    }
}
```

---

### 📄 `common/src/main/java/com/platform/common/model/reference/ReferenceUpsertRequest.java`

```java
package com.platform.common.model.reference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReferenceUpsertRequest {
    private String code;
    private Map<String, String> content;
}
```

---

### 📄 `common/src/main/java/com/platform/common/model/search/BatchReferenceRequest.java`

```java
package com.platform.common.model.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class BatchReferenceRequest {
    private List<SingleRequest> requests;

    @Data
    public static class SingleRequest {
        private String dictionaryCode;
        private List<String> keys;
    }
}
```

---

### 📄 `common/src/main/java/com/platform/common/model/search/EnrichedReferenceResponse.java`

```java
package com.platform.common.model.search;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.platform.common.model.reference.ReferenceResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EnrichedReferenceResponse {

    private String code;
    private ReferenceResponse content;

    @JsonIgnore
    @Builder.Default
    private Map<String, Object> attributes = new HashMap<>();

    @JsonAnyGetter
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @JsonAnySetter
    public void addAttribute(String key, Object value) {
        attributes.put(key, value);
    }
}
```

---

