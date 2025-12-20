package com.platform.server.controller;

import com.platform.common.model.lookup.DictionaryLookupResponse;
import com.platform.common.model.lookup.ItemLookupResponse;
import com.platform.common.model.lookup.LookupBatchRequest;
import com.platform.common.service.LookupService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1/lookup")
@RequiredArgsConstructor
public class LookupController {

    private final LookupService lookupService;

    @GetMapping("/{code}")
    public ResponseEntity<DictionaryLookupResponse> get(
            @PathVariable String code,
            @RequestParam(required = false) String lang,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant date,
            WebRequest request) {

        DictionaryLookupResponse response = lookupService.getDictionary(code, lang, date);

        String etag = "\"" + Integer.toHexString(response.hashCode()) + "\"";

        if (request.checkNotModified(etag)) {
            return null;
        }

        return ResponseEntity.ok()
                .eTag(etag)
                .cacheControl(CacheControl.maxAge(24, TimeUnit.HOURS))
                .body(response);
    }

    @GetMapping("/{code}/{key}")
    public ResponseEntity<ItemLookupResponse> get(
            @PathVariable String code,
            @PathVariable String key,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant date) {

        ItemLookupResponse response = lookupService.getItem(code, key, date);
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS))
                .body(response);
    }

    @PostMapping("/batch")
    public List<DictionaryLookupResponse> batch(@RequestBody LookupBatchRequest request) {
        return lookupService.getBatch(request);
    }
}