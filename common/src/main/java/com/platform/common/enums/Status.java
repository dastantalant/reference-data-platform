package com.platform.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Status {
    DRAFT(10),
    ACTIVE(20),
    ARCHIVED(30)
    ;
    private final int code;
}
