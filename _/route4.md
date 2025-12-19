Ниже представлена полная документация API роутов с указанием названий DTO классов, которые мы спроектировали в ходе рефакторинга.

---

# 📚 Reference Data Platform API

**Base URL:** `/api/v1`
**Формат даты:** ISO 8601 (`2025-12-31T23:59:59Z`)
**Формат тела:** JSON

---

## 1. 🚀 Public Lookup API (Для клиентов)
*Предназначен для фронтенда и микросервисов. Максимально оптимизирован, поддерживает кэширование (ETag).*

**Base Path:** `/api/v1/lookup`

### 1.1 Получить справочник целиком
Возвращает дерево: Код -> Заголовки -> Список элементов.

*   **Метод:** `GET /{code}`
*   **Параметры (Query):**
    *   `lang` (String, опц.) — Язык для фильтрации `i18n`. Если не указан, возвращает все.
    *   `date` (ISO Date, опц.) — Дата актуальности (Time-Travel). Если не указана, берется `now()`.
*   **Response DTO:** `DictionaryLookupResponse`

```json
// DictionaryLookupResponse
{
  "code": "COUNTRY",
  "i18n": { "ru": "Страны", "en": "Countries" },
  "content": [
    {
      "ref_key": "USA",
      "i18n": { "ru": "США", "en": "United States" },
      "details": { "iso": 840, "currency": "USD" } // common_content
    }
  ]
}
```

### 1.2 Получить одну запись
*   **Метод:** `GET /{code}/{key}`
*   **Параметры:** `date` (опц.)
*   **Response DTO:** `ItemLookupResponse`

```json
// ItemLookupResponse
{
  "ref_key": "USA",
  "i18n": { "ru": "США", "en": "United States" },
  "details": { "iso": 840 }
}
```

### 1.3 Пакетная загрузка (Multi-Dictionary)
Загрузка нескольких справочников за один HTTP вызов.

*   **Метод:** `POST /batch`
*   **Request DTO:** `LookupBatchRequest`
*   **Response DTO:** `List<DictionaryLookupResponse>`

```json
// LookupBatchRequest
{
  "requests": [
    { "dictionaryCode": "COUNTRY" },
    { "dictionaryCode": "CURRENCY", "keys": ["USD", "EUR"] }
  ]
}
```

---

## 2. 🛡️ Admin API: Definitions (Схемы)
*Управление метаданными справочников.*

**Base Path:** `/api/v1/admin/definitions`

### 2.1 Список справочников
*   **Метод:** `GET /`
*   **Параметры:** `page`, `size`, `search`
*   **Response DTO:** `PagedDefinitionResponse<DefinitionResponse>`

### 2.2 Создать справочник
*   **Метод:** `POST /`
*   **Request DTO:** `DefinitionCreateRequest`
*   **Response DTO:** `DefinitionResponse`

```json
// DefinitionCreateRequest
{
  "code": "COUNTRY",
  "translations": [
    { "locale": "ru", "value": "Страны" }
  ],
  "schema": { ...json schema... },
  "validation_rules": [ ... ]
}
```

### 2.3 Получить детали справочника
*   **Метод:** `GET /{code}`
*   **Response DTO:** `DefinitionResponse`

### 2.4 Управление версиями (Структура)
*   `GET /{code}/versions` — История версий.
*   `POST /{code}/versions` — Создать новую версию схемы (черновик).
*   `PATCH /{code}/versions/{version}/publish` — Сделать версию активной.

---

## 3. 🛡️ Admin API: Items (Данные)
*CRUD операции над записями. Полный формат данных.*

**Base Path:** `/api/v1/admin/definitions/{code}/items`

### 3.1 Поиск и фильтрация записей
*   **Метод:** `GET /`
*   **Параметры:**
    *   `q` (поиск по тексту)
    *   `page`, `size`, `sort`
    *   `status` (ACTIVE, DRAFT, ARCHIVED)
*   **Response DTO:** `PagedDefinitionResponse<ReferenceItemResponse>`

### 3.2 Получить запись (Full Admin View)
*   **Метод:** `GET /{key}`
*   **Response DTO:** `ReferenceItemResponse`

```json
// ReferenceItemResponse
{
  "ref_key": "USA",
  "code": "COUNTRY",
  "status": "ACTIVE",
  "parent_key": "NORTH_AMERICA",
  "valid_from": "2024-01-01T00:00:00Z",
  "valid_to": null,
  "common_content": { "iso": 840 },
  "translations": [
    { "locale": "ru", "value": "США" },
    { "locale": "en", "value": "USA" }
  ],
  "created_by": "admin",
  "created_at": "..."
}
```

### 3.3 Создать / Обновить (Upsert)
*   **Метод:** `POST /` (или `PUT /{key}`)
*   **Request DTO:** `ItemUpsertRequest`

```json
// ItemUpsertRequest
{
  "ref_key": "USA",
  "parent_key": "NORTH_AMERICA", // Опционально
  "valid_from": "2024-01-01T00:00:00Z", // Опционально
  "common_content": { ... },
  "translations": [
    { "locale": "ru", "value": "США" }
  ]
}
```

### 3.4 Массовый импорт (Batch Upsert)
*   **Метод:** `POST /batch`
*   **Request DTO:** `List<ItemUpsertRequest>`
*   **Response:** `200 OK` (или отчет об ошибках валидации)

### 3.5 Удаление (Архивация)
*   **Метод:** `DELETE /{key}`
*   **Response:** `204 No Content`

### 3.6 Утверждение (Workflow)
Перевод из `DRAFT` в `ACTIVE`.
*   **Метод:** `POST /{key}/approve`
*   **Response:** `200 OK`

---

## 4. ⚙️ System API (Обслуживание)

**Base Path:** `/api/v1/admin/system`

### 4.1 Сброс кэша
*   **Метод:** `DELETE /cache/{code}`
*   **Описание:** Принудительная инвалидация кэша Lookup API для справочника.

### 4.2 Перевалидация данных
*   **Метод:** `POST /validate/{code}`
*   **Описание:** Запуск фоновой задачи на проверку соответствия данных текущей JSON-схеме и правилам Cross-Reference.

### 4.3 Экспорт
*   **Метод:** `GET /export/{code}`
*   **Параметры:** `format=xlsx` или `json`.
*   **Response:** Файл.

---

## 🗺️ Сводная таблица DTO

| Название класса DTO | Пакет | Назначение |
| :--- | :--- | :--- |
| **TranslationDto** | `.common` | Универсальный объект `{locale, value}`. |
| **DictionaryLookupResponse** | `.lookup` | Ответ публичного API (Дерево справочника). |
| **ItemLookupResponse** | `.lookup` | Элемент внутри публичного ответа (Map i18n). |
| **LookupBatchRequest** | `.lookup` | Запрос на получение пачки справочников. |
| **DefinitionCreateRequest** | `.definition` | Создание метаданных справочника. |
| **DefinitionResponse** | `.definition` | Ответ админки по справочнику. |
| **PagedDefinitionResponse** | `.definition` | Обертка для пагинации. |
| **ReferenceItemResponse** | `.item` | Полная карточка записи (для админки). |
| **ItemUpsertRequest** | `.item` | Создание/Обновление записи. |




Вот подробное описание форматов данных (**Request Body** и **Response Body**) для каждого из указанных вами роутов.

Они базируются на ранее утвержденных DTO: `DefinitionResponse`, `DefinitionCreateRequest`, `ItemUpsertRequest` и `ReferenceItemResponse`.

---

## 1. Admin API: Definitions (Схемы)

### 1.1 Получить историю версий
**GET** `/api/v1/admin/definitions/{code}/versions`

*   **Request:** `(пустое тело)`
*   **Response:** `List<DefinitionResponse>`
    Возвращает список всех версий (v1, v2...) отсортированный по убыванию версии.

```json
[
  {
    "code": "COUNTRY",
    "version": 2,
    "status": "DRAFT",
    "is_current": false,
    "schema": { "type": "object", "properties": { "iso": { "type": "number" }, "population": { "type": "number" } } },
    "translations": [ { "locale": "ru", "value": "Страны" } ],
    "created_at": "2025-12-20T10:00:00Z",
    "created_by": "admin"
  },
  {
    "code": "COUNTRY",
    "version": 1,
    "status": "ACTIVE",
    "is_current": true,
    "schema": { "type": "object", "properties": { "iso": { "type": "number" } } },
    "translations": [ { "locale": "ru", "value": "Страны" } ],
    "created_at": "2025-01-01T10:00:00Z",
    "created_by": "system"
  }
]
```

### 1.2 Создать новую версию (Черновик)
**POST** `/api/v1/admin/definitions/{code}/versions`

*   **Request:** `DefinitionCreateRequest`
    *   Поле `code` в теле игнорируется (берется из URL), или должно совпадать.
    *   Передаем новую структуру `schema` и правила.

```json
{
  "schema": {
    "type": "object",
    "properties": {
      "iso": { "type": "number" },
      "new_field": { "type": "string" } 
    },
    "required": ["iso"]
  },
  "translations": [ 
    { "locale": "ru", "value": "Страны (v2)" }
  ],
  "validation_rules": [
    { "field": "currency_code", "targetDictionary": "CURRENCY" }
  ]
}
```

*   **Response:** `DefinitionResponse` (Созданная версия)

```json
{
  "code": "COUNTRY",
  "version": 2,  // Авто-инкремент
  "status": "DRAFT",
  "is_current": false,
  "schema": { ... },
  "created_at": "2025-12-20T12:00:00Z"
  // ... остальные поля
}
```

### 1.3 Опубликовать версию
**PATCH** `/api/v1/admin/definitions/{code}/versions/{version}/publish`

*   **Request:** `(пустое тело)`
*   **Response:** `DefinitionResponse` (Обновленная)
    *   Старая версия становится `ARCHIVED` (или просто `is_current: false`).
    *   Эта версия становится `ACTIVE` и `is_current: true`.

```json
{
  "code": "COUNTRY",
  "version": 2,
  "status": "ACTIVE",
  "is_current": true,
  // ...
}
```

---

## 2. Admin API: Items (Данные)

### 2.1 Создать / Обновить запись (Upsert)
**PUT** `/api/v1/admin/definitions/{code}/items/{key}`

*   **Request:** `ItemUpsertRequest`
    *   `ref_key` в теле должен совпадать с `{key}` в URL (или быть опущен).

```json
{
  "parent_key": "NORTH_AMERICA",  // Опционально (Иерархия)
  "valid_from": "2025-01-01T00:00:00Z", // Опционально (Темпоральность)
  "valid_to": null,
  
  "common_content": {
    "iso_numeric": 840,
    "currency_code": "USD"
  },
  
  "translations": [
    { "locale": "ru", "value": "США" },
    { "locale": "en", "value": "USA" }
  ]
}
```

*   **Response:** `ReferenceItemResponse`

```json
{
  "code": "COUNTRY",
  "ref_key": "USA",
  "status": "DRAFT", // При создании обычно DRAFT, если нет авто-публикации
  "parent_key": "NORTH_AMERICA",
  "valid_from": "2025-01-01T00:00:00Z",
  "common_content": { "iso_numeric": 840, "currency_code": "USD" },
  "translations": [
    { "locale": "ru", "value": "США" },
    { "locale": "en", "value": "USA" }
  ],
  "created_by": "user_1",
  "updated_at": "2025-12-20T12:05:00Z"
}
```

### 2.2 Пакетная вставка
**POST** `/api/v1/admin/definitions/{code}/items/batch`

*   **Request:** `List<ItemUpsertRequest>`

```json
[
  {
    "ref_key": "USA",
    "common_content": { "iso": 840 },
    "translations": [{ "locale": "en", "value": "USA" }]
  },
  {
    "ref_key": "CAN",
    "common_content": { "iso": 124 },
    "translations": [{ "locale": "en", "value": "Canada" }]
  }
]
```

*   **Response:** `BatchResponse` (Рекомендуется обертка, чтобы показать ошибки)

```json
{
  "processed_count": 2,
  "failed_count": 0,
  "items": [
    { "ref_key": "USA", "status": "CREATED" }, // Или полные объекты ReferenceItemResponse
    { "ref_key": "CAN", "status": "UPDATED" }
  ]
}
```

### 2.3 Удалить (Архивировать)
**DELETE** `/api/v1/admin/definitions/{code}/items/{key}`

*   **Request:** `(пустое тело)`
*   **Response:** `(пустое тело)` — **Status 204 No Content**

### 2.4 Утвердить (Approve)
**POST** `/api/v1/admin/definitions/{code}/items/{key}/approve`

*   **Request:** `(пустое тело)`
*   **Response:** `ReferenceItemResponse`
    *   Поле `status` меняется на `ACTIVE`.

```json
{
  "ref_key": "USA",
  "status": "ACTIVE",
  // ... остальные поля
}
```

---

## 3. System API

### 3.1 Запуск валидации
**POST** `/api/v1/admin/system/validate/{code}`

*   **Request:** `(пустое тело)` или опциональные настройки

```json
// Опционально
{
  "full_scan": true  // Перепроверить даже то, что не менялось
}
```

*   **Response:** `ValidationReportResponse`
    Если валидация быстрая (синхронная):

```json
{
  "dictionary_code": "COUNTRY",
  "total_items": 250,
  "valid_items": 249,
  "invalid_items": 1,
  "errors": [
    {
      "ref_key": "ATLANTIS",
      "message": "Validation error: required field 'iso' is missing"
    }
  ]
}
```

*Если валидация асинхронная (рекомендуется для больших объемов), возвращается `202 Accepted` и `jobId`.*