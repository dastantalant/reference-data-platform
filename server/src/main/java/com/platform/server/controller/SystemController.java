package com.platform.server.controller;

import com.platform.common.model.system.ValidationReportResponse;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/system")
public class SystemController {
    @DeleteMapping("/cache/{code}")
    public void  deleteCache(@PathVariable String code){
    }

    @PostMapping("/validate/{code}")
    public ValidationReportResponse  validate(@PathVariable String code){
        return null;
    }

    @GetMapping("/export/{code}")
    public byte[] export(@PathVariable String code){
        return null;
    }
}
