base_definition

```json
{
  "code": "LANGUAGE",
  "version": 1,
  "status": "ACTIVE",
  "is_current": true
}
```

definition_page_T

```json
{
  "number": 1,
  "size": 10,
  "totalPages": 5,
  "totalElements": 10,
  
  "code": "LANGUAGE",
  "version": 1,
  "status": "ACTIVE",
  "is_current": true,
  "schema": {},
  "content": ["T"],
  
  "created_by": "username",
  "created_at": "2025-10-20T10:00:00Z",
  "updated_by": "username",
  "updated_at": "2025-10-20T10:00:00Z",
  "deleted_by": "username",
  "deleted_at": "2025-10-20T10:00:00Z"
}
```

definition_response

```json
{
  "code": "LANGUAGE",
  "version": 1,
  "status": "ACTIVE",
  "is_current": true,
  "schema": {},
  
  "created_by": "username",
  "created_at": "2025-10-20T10:00:00Z",
  "updated_by": "username",
  "updated_at": "2025-10-20T10:00:00Z",
  "deleted_by": "username",
  "deleted_at": "2025-10-20T10:00:00Z"
}
```

reference_item_active_response

```json
{
  "version": 1,
  "ref_key": "en",
  "content": {},
  
  "created_by": "username",
  "created_at": "2025-10-20T10:00:00Z",
  "updated_by": "username",
  "updated_at": "2025-10-20T10:00:00Z",
  "deleted_by": "username",
  "deleted_at": "2025-10-20T10:00:00Z"
}
```

reference_item_response

```json
{
  "version": 1,
  "ref_key": "en",
  "status": "ACTIVE",
  "content": {},
  
  "created_by": "username",
  "created_at": "2025-10-20T10:00:00Z",
  "updated_by": "username",
  "updated_at": "2025-10-20T10:00:00Z",
  "deleted_by": "username",
  "deleted_at": "2025-10-20T10:00:00Z"
}
```

reference_single_item_response

```json
{
  "version": 1,
  "ref_key": "en",
  "status": "ACTIVE",
  "definition": {
    "code": "LANGUAGE",
    "version": 1,
    "status": "ACTIVE",
    "is_current": true
  },
  "content": {},
  
  "created_by": "username",
  "created_at": "2025-10-20T10:00:00Z",
  "updated_by": "username",
  "updated_at": "2025-10-20T10:00:00Z",
  "deleted_by": "username",
  "deleted_at": "2025-10-20T10:00:00Z"
}
```

response
```json
[
  {
    "code": "LANGUAGE",
    "i18n": {
      "en": "Languages",
      "ru": "Языки",
      "ky": "Тилдер"
    },
    "content": [
      {
        "ref_key": "en",
        "i18n": {
          "en": "English",
          "ru": "Английский",
          "ky": "Англис тили"
        }
      },
      {
        "ref_key": "ru",
        "i18n": {
          "en": "Russian",
          "ru": "Русский",
          "ky": "Орус тили"
        }
      },
      {
        "ref_key": "ky",
        "i18n": {
          "en": "Kyrgyz",
          "ru": "Киргизский",
          "ky": "Кыргыз тили"
        }
      }
    ]
  },
  {
    "code": "COUNTRY",
    "content": [
      {
        "ref_key": "USA",
        "i18n": {
          "en": "United States",
          "ru": "Соединенные Штаты Америки",
          "ky": "Америка Кошмо Штаттары"
        }
      },
      {
        "ref_key": "Kyrgyzstan",
        "i18n": {
          "en": "Kyrgyzstan",
          "ru": "Кыргызстан",
          "ky": "Кыргызстан"
        }
      }
    ]
  }
]

```

response_model
```json
{
  "datetime": "2025-10-20T10:00:00Z",
  "request": [
    {
      "code": "COUNTRY"
    },
    {
      "code": "CURRENCY",
      "keys": [
        "USD",
        "EUR"
      ]
    }
  ],
  "response": {}
}
```