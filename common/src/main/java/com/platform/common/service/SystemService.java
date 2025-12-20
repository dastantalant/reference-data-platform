package com.platform.common.service;

import com.platform.common.model.system.ValidationReportResponse;

public interface SystemService {

    void evictCache(String code);

    ValidationReportResponse validateDictionary(String code);

    byte[] export(String code);
}
