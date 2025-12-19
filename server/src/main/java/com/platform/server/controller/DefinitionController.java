package com.platform.server.controller;

import com.platform.common.model.definition.DefinitionCreateRequest;
import com.platform.common.model.definition.DefinitionResponse;
import com.platform.common.model.definition.PagedDefinitionResponse;
import com.platform.common.model.item.ItemUpsertRequest;
import com.platform.common.model.reference.ReferenceItemResponse;

import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/definitions")
public class DefinitionController {

    @GetMapping
    public PagedDefinitionResponse<DefinitionResponse> page(Pageable pageable){
        return null;
    }

    @PostMapping
    public DefinitionResponse post(@RequestBody DefinitionCreateRequest request){
        return null;
    }

    @GetMapping("/{code}")
    public DefinitionResponse get(@PathVariable String code){
        return null;
    }

    @GetMapping("/{code}/versions")
    public List<DefinitionResponse> getVersions(@PathVariable String code){
        return null;
    }

    @PostMapping("/{code}/versions")
    public DefinitionResponse post(@PathVariable String code, @RequestBody DefinitionCreateRequest  request){
        return null;
    }

    @PatchMapping("/{code}/versions/{version}/publish")
    public DefinitionResponse patch(@PathVariable String code, @PathVariable int version){
        return null;
    }

    @GetMapping("/{code}/items")
    public PagedDefinitionResponse<ReferenceItemResponse> getItem(@PathVariable String code, Pageable pageable){
        return null;
    }

    @GetMapping("/{code}/items/{key}")
    public ReferenceItemResponse  getItem(@PathVariable String code, @PathVariable String key){
        return null;
    }

    @PutMapping("/{code}/items/{key}")
    public ReferenceItemResponse  putItem(@PathVariable String code, @PathVariable String key, @RequestBody ItemUpsertRequest request){
        return null;
    }

    @PostMapping("/{code}/items/batch")
    public DefinitionResponse batchPost(@PathVariable String code, @RequestBody List<ItemUpsertRequest>  request){
        return null;
    }

    @DeleteMapping("/{code}/items/{key}")
    public void delete(@PathVariable String code, @PathVariable String key){
    }

    @PostMapping("/{code}/items/{key}/approve")
    public ReferenceItemResponse  approve(@PathVariable String code, @PathVariable String key){
        return null;
    }
}
