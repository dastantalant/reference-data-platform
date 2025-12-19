package com.platform.common.service.impl;

import com.platform.common.entity.Definition;
import com.platform.common.enums.Status;
import com.platform.common.mapper.DefinitionMapper;
import com.platform.common.model.definition.DefinitionCreateRequest;
import com.platform.common.model.definition.DefinitionResponse;
import com.platform.common.model.definition.PagedDefinitionResponse;
import com.platform.common.repository.DefinitionRepository;
import com.platform.common.service.DefinitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DefinitionServiceImpl implements DefinitionService {

    private final DefinitionRepository repository;
    private final DefinitionMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public PagedDefinitionResponse<DefinitionResponse> findAll(Pageable pageable, String search) {
        Page<Definition> page = (search == null)
                ? repository.findAll(pageable)
                : repository.searchByCodeOrName(search, pageable);

        List<DefinitionResponse> content = page.getContent().stream()
                .map(mapper::toResponse)
                .toList();

        return PagedDefinitionResponse.<DefinitionResponse>builder()
                .content(content)
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .currentPage(page.getNumber())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public DefinitionResponse getByCode(String code) {
        Definition def = repository.findCurrentByCode(code)
                .orElseThrow(() -> new RuntimeException("Definition not found: " + code));
        return mapper.toResponse(def);
    }

    @Override
    public DefinitionResponse create(DefinitionCreateRequest request) {
        if (repository.existsByCode(request.getCode())) {
            throw new RuntimeException("Definition already exists: " + request.getCode());
        }

        Definition def = Definition.builder()
                .id(new Definition.Id(request.getCode(), 1))
                .status(Status.DRAFT)
                .isCurrent(true)
                .schemaLob(mapper.toJson(request.getSchema()))
                .translations(mapper.toEntityTranslations(request.getTranslations()))
                .build();

        return mapper.toResponse(repository.save(def));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DefinitionResponse> getVersions(String code) {
        return repository.findAllByIdCodeOrderByIdVersionDesc(code).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public DefinitionResponse createVersion(String code, DefinitionCreateRequest request) {
        int maxVersion = repository.findMaxVersion(code).orElse(0);

        Definition def = Definition.builder()
                .id(new Definition.Id(code, maxVersion + 1))
                .status(Status.DRAFT)
                .isCurrent(false)
                .schemaLob(mapper.toJson(request.getSchema()))
                .translations(mapper.toEntityTranslations(request.getTranslations()))
                .build();

        return mapper.toResponse(repository.save(def));
    }

    @Override
    public DefinitionResponse publishVersion(String code, int version) {
        repository.findCurrentByCode(code).ifPresent(d -> {
            d.setCurrent(false);
            if (d.getStatus() == Status.ACTIVE) d.setStatus(Status.ARCHIVED);
            repository.save(d);
        });

        Definition target = repository.findById(new Definition.Id(code, version))
                .orElseThrow(() -> new RuntimeException("Version not found"));

        target.setStatus(Status.ACTIVE);
        target.setCurrent(true);

        return mapper.toResponse(repository.save(target));
    }
}