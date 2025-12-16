Ниже представлен **оптимизированный REST API контракт**.

### Основные принципы оптимизации в этом дизайне:
1.  **Разделение ответственности:** Четкое разделение на **Lookup API** (для систем-потребителей, read-only, max caching) и **Admin API** (для управления, валидации, аудита).
2.  **Вложенность ресурсов:** URL отражает иерархию `Справочник -> Записи`.
3.  **Batch-операции:** Массовое создание и получение данных для снижения RTT (Round Trip Time).
4.  **Управление трафиком:** Использование HTTP заголовков для кэширования (`ETag`, `Last-Modified`).

---

## 1. Public Lookup API (Высокая производительность)
*Предназначен для микросервисов и фронтенда. Максимально быстрый, использует DTO проекции.*

**Base URL:** `/api/v1/lookup`

| Метод | URL | Тело запроса / Параметры | Описание |
| :--- | :--- | :--- | :--- |
| **GET** | `/{code}` | `?v=1` (опц. версия)<br>`?page=0&size=100`<br>`?updatedAfter={timestamp}` | **Получить записи справочника.**<br>Поддерживает `If-Modified-Since`. Если `updatedAfter` передан, возвращает только дельту изменений. |
| **GET** | `/{code}/{key}` | - | **Получить одну запись.**<br>Возвращает JSON контент + `ref_key`. |
| **HEAD**| `/{code}/{key}` | - | **Проверка существования.**<br>Возвращает `200 OK` или `404 Not Found`. Тело пустое. Быстро для валидации. |
| **POST**| `/batch` | `BatchFetchRequest`:<br>`{"requests": [{"code": "A", "keys": ["1", "2"]}, {"code": "B"}]}` | **Мульти-справочный запрос.**<br>Позволяет за один вызов получить данные из разных справочников (например, при загрузке страницы). |

---

## 2. Admin Definition API (Управление структурой)
*Управление метаданными и JSON-схемами.*

**Base URL:** `/api/v1/admin/definitions`

| Метод | URL | Тело запроса | Описание |
| :--- | :--- | :--- | :--- |
| **GET** | `/` | `?page=0&size=20`<br>`?search={text}` | **Список справочников.**<br>Возвращает метаданные (код, название, статус), без схем и данных. |
| **POST**| `/` | `CreateDefinitionRequest`:<br>`{"code": "COUNTRY", "name": "..."}` | **Регистрация нового справочника.**<br>Создает запись, но без активной схемы. |
| **GET** | `/{code}` | - | **Детали справочника.**<br>Возвращает текущую активную схему и настройки. |
| **PUT** | `/{code}` | `UpdateDefinitionRequest` | Обновление названия/описания (не схемы). |
| **GET** | `/{code}/versions` | - | **История версий схемы.** |
| **POST**| `/{code}/versions` | `CreateSchemaRequest`:<br>`{"schema": {...}, "isDraft": true}` | **Создание новой версии схемы.** |
| **PATCH**| `/{code}/versions/{ver}`| `{"status": "ACTIVE"}` | **Публикация версии.**<br>Делает версию текущей. Старая версия архивируется. |

---

## 3. Admin Item API (Управление данными)
*CRUD операций над записями. Включает валидацию по схеме.*

**Base URL:** `/api/v1/admin/definitions/{code}/items`

| Метод | URL | Тело запроса / Параметры | Описание |
| :--- | :--- | :--- | :--- |
| **GET** | `/` | `?q=name:Germany`<br>`?page=0&size=20` | **Поиск записей.**<br>Возвращает `PagedDefinitionResponse`. Поля фильтруются по `@JsonView(Summary)`. |
| **POST**| `/batch` | `[{"key": "DE", "content": {...}}]` | **Массовая вставка/обновление.**<br>Транзакционно сохраняет пачку записей. Критично для импорта. |
| **POST**| `/` | `{"key": "DE", "content": {...}}` | **Создать одну запись.**<br>Валидирует json `content` по активной схеме. |
| **GET** | `/{key}` | - | **Получить запись (Full).**<br>Возвращает полные данные, включая аудит (`createdBy`, `updatedAt`). |
| **PUT** | `/{key}` | `{"content": {...}}` | **Полное обновление записи.** |
| **PATCH**| `/{key}` | `{"content": {"population": 500}}` | **Частичное обновление.**<br>Merge текущего JSON с пришедшим. |
| **DELETE**| `/{key}` | - | **Soft Delete.**<br>Переводит статус записи в `DELETED`. |
| **GET** | `/{key}/history` | `?page=0` | **Аудит записи.**<br>Кто и когда менял эту запись (Envers или Audit table). |

---

## 4. Maintenance & Utilities (Служебные)

**Base URL:** `/api/v1/admin/maintenance`

| Метод | URL | Описание |
| :--- | :--- | :--- |
| **POST** | `/validate/{code}` | **Перевалидация.**<br>Запускает фоновую задачу: проверить все записи справочника `{code}` на соответствие *текущей* схеме (нужно после смены схемы). |
| **POST** | `/validate/dry-run` | **Тест схемы.**<br>Принимает `{ "schema": {...}, "data": {...} }`. Проверяет данные без сохранения. Для UI редакторов. |
| **DELETE**| `/cache/{code}` | **Сброс кэша.**<br>Принудительная инвалидация кэша для справочника. |
| **GET** | `/export/{code}` | **Экспорт.**<br>Скачать JSON файл со схемой и данными. |
| **POST** | `/import` | **Импорт.**<br>Загрузить бэкап справочника. |

---

## 5. Примеры DTO (С учетом рекомендаций)

Чтобы этот API работал эффективно, ваши DTO должны выглядеть примерно так:

**`BatchReferenceRequest.java`** (для `/api/v1/lookup/batch`)
```java
@Data
public class BatchReferenceRequest {
    // Список кодов справочников, которые нужны клиенту
    private List<SingleRequest> requests;

    @Data
    public static class SingleRequest {
        private String dictionaryCode;
        private List<String> keys; // Если null/empty -> вернуть все (осторожно!)
    }
}
```

**`ReferenceItemResponse.java`** (Единый ответ)
```java
@Getter @Setter @Builder
public class ReferenceItemResponse extends AuditableDto {
    @JsonView(Views.Summary.class)
    private String code;       // "COUNTRY"
    
    @JsonView(Views.Summary.class)
    private String refKey;     // "DE"
    
    @JsonView(Views.Public.class)
    private Map<String, Object> content; // {"name": "Germany"}
    
    // Status и аудит поля придут из AuditableDto
    // и будут видны только в Views.Internal или Views.Audit
}
```

### Технические требования для реализации:
1.  **ETags:** На `GET /lookup/{code}` сервер должен считать хэш от всех данных. Если клиент присылает заголовок `If-None-Match: "hash123"`, и данные не менялись, сервер отвечает `304 Not Modified` (тело пустое). Это экономит 99% трафика.
2.  **Async Validation:** Методы `/validate/{code}` должны быть асинхронными (возвращать `202 Accepted` и ID задачи), так как проверка 100к записей может занять время.
3.  **Validation Exception:** При ошибке валидации (`400 Bad Request`) возвращать структурированный JSON с путями к ошибкам: `[{"field": "content.currency", "error": "required"}]`.