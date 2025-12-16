package com.platform.common.model.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.platform.common.model.view.Views;

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

    @JsonView(Views.Audit.class)
    @JsonProperty("created_by")
    private String createdBy;

    @JsonView(Views.Audit.class)
    @JsonProperty("created_at")
    private Instant createdAt;

    @JsonView(Views.Audit.class)
    @JsonProperty("updated_by")
    private String updatedBy;

    @JsonView(Views.Audit.class)
    @JsonProperty("updated_at")
    private Instant updatedAt;

    @JsonView(Views.Audit.class)
    @JsonProperty("deleted_by")
    private String deletedBy;

    @JsonView(Views.Audit.class)
    @JsonProperty("deleted_at")
    private Instant deletedAt;
}