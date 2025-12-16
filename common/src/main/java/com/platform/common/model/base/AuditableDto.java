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
