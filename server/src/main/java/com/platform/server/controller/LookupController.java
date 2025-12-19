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

        // 1. Получаем данные (Сервис берет их из Caffeine кэша памяти, это миллисекунды)
        DictionaryLookupResponse response = lookupService.getDictionary(code, lang, date);

        // 2. Генерируем ETag на основе содержимого объекта.
        // Lombok @Data генерирует hashCode на основе всех полей, это надежно для DTO.
        // Преобразуем в Hex-строку для использования в HTTP заголовке.
        String etag = "\"" + Integer.toHexString(response.hashCode()) + "\"";

        // 3. Проверка ETag средствами Spring
        // Если клиент прислал заголовок If-None-Match, совпадающий с нашим etag,
        // этот метод вернет true, и Spring сам отправит статус 304 (без тела).
        if (request.checkNotModified(etag)) {
            return null; // Тело не нужно, Spring завершит обработку
        }

        // 4. Если данные изменились (или это первый запрос), отдаем 200 OK + новый ETag
        return ResponseEntity.ok()
                .eTag(etag)
                // max-age=86400 (24 часа).
                // Браузер будет держать кэш сутки. После суток он сделает запрос с ETag.
                // Если данные не поменялись, сервер ответит 304, и браузер продлит кэш еще на сутки.
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