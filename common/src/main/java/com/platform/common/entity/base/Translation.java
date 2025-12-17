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
public class Translation {

    @Column(name = "locale", nullable = false, length = 2)
    private String locale;

    @Column(name = "value", nullable = false, length = 2000)
    private String value;

}