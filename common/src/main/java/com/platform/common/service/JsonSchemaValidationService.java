package com.platform.common.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JsonSchemaValidationService {

    private final ObjectMapper objectMapper;

}