package com.platform.server.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.platform.common.enums.Status;
import com.platform.common.model.definition.DefinitionCreateRequest;
import com.platform.common.model.definition.DefinitionResponse;
import com.platform.common.model.definition.PagedDefinitionResponse;
import com.platform.common.model.item.BatchResponse;
import com.platform.common.model.item.ItemUpsertRequest;
import com.platform.common.model.reference.ReferenceItemResponse;
import com.platform.common.model.view.Views;
import com.platform.common.service.DefinitionService;
import com.platform.common.service.ReferenceItemService;

import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/definitions")
@RequiredArgsConstructor
public class DefinitionController {

    private final DefinitionService definitionService;
    private final ReferenceItemService itemService;

    @GetMapping
    @JsonView(Views.Summary.class)
    public PagedDefinitionResponse<DefinitionResponse> page(
            @RequestParam(required = false) String search,
            Pageable pageable) {
        return definitionService.findAll(pageable, search);
    }

    @PostMapping
    @JsonView(Views.Internal.class)
    public DefinitionResponse post(@RequestBody DefinitionCreateRequest request) {
        return definitionService.create(request);
    }

    @GetMapping("/{code}")
    @JsonView(Views.Internal.class)
    public DefinitionResponse get(@PathVariable String code) {
        return definitionService.getByCode(code);
    }

    @GetMapping("/{code}/versions")
    @JsonView(Views.Internal.class)
    public List<DefinitionResponse> getVersions(@PathVariable String code) {
        return definitionService.getVersions(code);
    }

    @PostMapping("/{code}/versions")
    @JsonView(Views.Internal.class)
    public DefinitionResponse post(@PathVariable String code, @RequestBody DefinitionCreateRequest request) {
        return definitionService.createVersion(code, request);
    }

    @PatchMapping("/{code}/versions/{version}/publish")
    @JsonView(Views.Internal.class)
    public DefinitionResponse patch(@PathVariable String code, @PathVariable int version) {
        return definitionService.publishVersion(code, version);
    }

    @GetMapping("/{code}/items")
    @JsonView(Views.Summary.class)
    public PagedDefinitionResponse<ReferenceItemResponse> getItem(
            @PathVariable String code,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Status status,
            Pageable pageable) {
        return itemService.findAll(code, pageable, q, status);
    }

    @GetMapping("/{code}/items/{key}")
    @JsonView(Views.Internal.class)
    public ReferenceItemResponse getItem(@PathVariable String code, @PathVariable String key) {
        return itemService.findByKey(code, key);
    }

    @PutMapping("/{code}/items/{key}")
    @JsonView(Views.Internal.class)
    public ReferenceItemResponse putItem(@PathVariable String code, @PathVariable String key, @RequestBody ItemUpsertRequest request) {
        return itemService.upsert(code, key, request);
    }

    @PostMapping("/{code}/items/batch")
    public BatchResponse batchPost(@PathVariable String code, @RequestBody List<ItemUpsertRequest> request) {
        return itemService.batchUpsert(code, request);
    }

    @DeleteMapping("/{code}/items/{key}")
    public void delete(@PathVariable String code, @PathVariable String key) {
        itemService.delete(code, key);
    }

    @PostMapping("/{code}/items/{key}/approve")
    @JsonView(Views.Internal.class)
    public ReferenceItemResponse approve(@PathVariable String code, @PathVariable String key) {
        return itemService.approve(code, key);
    }
}