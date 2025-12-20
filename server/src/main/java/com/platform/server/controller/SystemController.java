package com.platform.server.controller;

import com.platform.common.model.system.ValidationReportResponse;
import com.platform.common.service.SystemService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/system")
@RequiredArgsConstructor
public class SystemController {

    private final SystemService systemService;

    @DeleteMapping("/cache/{code}")
    public void deleteCache(@PathVariable String code) {
        systemService.evictCache(code);
    }

    @PostMapping("/validate/{code}")
    public ValidationReportResponse validate(@PathVariable String code) {
        return systemService.validateDictionary(code);
    }

    @GetMapping("/export/{code}")
    public ResponseEntity<byte[]> export(@PathVariable String code) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + code + ".xlsx\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(systemService.export(code));
    }
}