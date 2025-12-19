package com.platform.server.controller;

import com.platform.common.model.lookup.DictionaryLookupResponse;
import com.platform.common.model.lookup.ItemLookupResponse;
import com.platform.common.model.lookup.LookupBatchRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/lookup")
public class LookupController {

    @GetMapping("/{code}")
    public DictionaryLookupResponse get(@PathVariable String code) {
        return null;
    }

    @GetMapping("/{code}/{key}")
    public ItemLookupResponse get(@PathVariable String code, @PathVariable String key) {
        return null;
    }

    @PostMapping("/batch")
    public List<DictionaryLookupResponse> batch(@RequestBody LookupBatchRequest request){
        return null;
    }
}
