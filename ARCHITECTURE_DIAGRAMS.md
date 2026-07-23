# 📊 DIAGRAMA VISUAL DEL SISTEMA

## 🌐 Vista General

```
┌──────────────────────────────────────────────────────────────┐
│                      CLIENTE HTTP                            │
│              (Browser, cURL, Postman, Mobile)                │
└──────────────────────────┬───────────────────────────────────┘
                           │
                    HTTP REQUEST/RESPONSE
                           │
        ┌──────────────────▼───────────────────┐
        │     QUARKUS REST ENDPOINT            │
        │   (Port 8080, /api/...)              │
        └──────────────────┬───────────────────┘
                           │
┌──────────────────────────▼───────────────────────────────────┐
│           REST ADAPTER LAYER                                  │
│  ┌─────────────────────────────────────────────────────┐    │
│  │  ItemRestAdapter                                    │    │
│  │  ├─ listItems()        → ItemFilterQuery            │    │
│  │  ├─ createItem()       → ItemRequest DTO            │    │
│  │  ├─ getItem()          → ItemResponse DTO           │    │
│  │  ├─ updateItem()       → ItemResponse DTO           │    │
│  │  └─ toPaginatedItemsDTO() → Converts Domain to DTO │    │
│  ├─ CustomerRestAdapter (mismo patrón)                │    │
│  ├─ EmployeeRestAdapter (mismo patrón)                │    │
│  ├─ AppointmentRestAdapter (mismo patrón)             │    │
│  ├─ SaleRestAdapter (mismo patrón)                    │    │
│  ├─ ReportRestAdapter (mismo patrón)                  │    │
│  └─ AuditRestAdapter (mismo patrón)                   │    │
└──────────────────────────┬───────────────────────────────────┘
                           │
                  DTO → Domain Model Conversion
                           │
┌──────────────────────────▼───────────────────────────────────┐
│           APPLICATION LAYER (Services)                        │
│  ┌─────────────────────────────────────────────────────┐    │
│  │  ItemService                                        │    │
│  │  ├─ create(Item): Item                             │    │
│  │  ├─ update(Item): Item                             │    │
│  │  ├─ delete(id): void                               │    │
│  │  └─ Lógica de negocio y validaciones               │    │
│  ├─ CustomerService                                   │    │
│  ├─ EmployeeService                                   │    │
│  ├─ AppointmentService                                │    │
│  ├─ SaleService (+ cálculo de totales)               │    │
│  ├─ ReportService (+ agregación de datos)            │    │
│  └─ AuditService (+ registro de cambios)             │    │
└──────────────────────────┬───────────────────────────────────┘
                           │
            Inyección de dependencias (@Inject)
                           │
┌──────────────────────────▼───────────────────────────────────┐
│           PORT INTERFACES (Contracts)                         │
│  ┌─────────────────────────────────────────────────────┐    │
│  │  ItemRepositoryPort                                │    │
│  │  ├─ find(ItemFilterQuery): PagedResponse<Item>    │    │
│  │  ├─ count(ItemFilterQuery): long                  │    │
│  │  ├─ save(Item): Item                              │    │
│  │  ├─ findById(id): Item                            │    │
│  │  └─ delete(id): void                              │    │
│  ├─ CustomerRepositoryPort (misma interfaz)         │    │
│  ├─ EmployeeRepositoryPort (misma interfaz)         │    │
│  ├─ AppointmentRepositoryPort (misma interfaz)      │    │
│  ├─ SaleRepositoryPort (misma interfaz)             │    │
│  ├─ ReportRepositoryPort (misma interfaz)           │    │
│  └─ AuditRepositoryPort (misma interfaz)            │    │
└──────────────────────────┬───────────────────────────────────┘
                           │
           Implementación de interfaces
                           │
┌──────────────────────────▼───────────────────────────────────┐
│     PERSISTENCE ADAPTERS (JPA/Panache)                        │
│  ┌─────────────────────────────────────────────────────┐    │
│  │  ItemPersistenceAdapter                            │    │
│  │  ├─ find(ItemFilterQuery)                          │    │
│  │  │  ├─ buildQuery() → HQL dinámico                │    │
│  │  │  ├─ Filtros: search, category, active         │    │
│  │  │  ├─ Ordenamiento: sortBy, sortDirection        │    │
│  │  │  ├─ Paginación: offset, pageSize              │    │
│  │  │  └─ Retorna PagedResponse<Item>                │    │
│  │  ├─ save(Item) → ItemJpaEntity → persist()        │    │
│  │  ├─ toDomain() → JpaEntity to Domain              │    │
│  │  └─ toPersistence() → Domain to JpaEntity         │    │
│  ├─ CustomerPersistenceAdapter (mismo patrón)       │    │
│  ├─ EmployeePersistenceAdapter (mismo patrón)       │    │
│  ├─ AppointmentPersistenceAdapter (mismo patrón)    │    │
│  ├─ SalePersistenceAdapter (mismo patrón)           │    │
│  ├─ ReportPersistenceAdapter (mismo patrón)         │    │
│  └─ AuditPersistenceAdapter (mismo patrón)          │    │
└──────────────────────────┬───────────────────────────────────┘
                           │
              @ApplicationScoped + @Transactional
                           │
┌──────────────────────────▼───────────────────────────────────┐
│     JPA ENTITIES & PANACHE REPOSITORIES                       │
│  ┌─────────────────────────────────────────────────────┐    │
│  │  ItemJpaEntity (@Entity @Table("items"))           │    │
│  │  ├─ @Id @GeneratedValue id                        │    │
│  │  ├─ name, description, category, price            │    │
│  │  ├─ isActive, createdAt, updatedAt                │    │
│  │  └─ Mapea exactamente con tabla de BD             │    │
│  │                                                     │    │
│  │  ItemPanacheRepository extends PanacheRepository  │    │
│  │  ├─ Heredita find(), count(), persist()           │    │
│  │  ├─ Métodos adicionales si es necesario           │    │
│  │  └─ Usa Hibernate bajo el capó                    │    │
│  ├─ CustomerJpaEntity + CustomerPanacheRepository    │    │
│  ├─ EmployeeJpaEntity + EmployeePanacheRepository    │    │
│  ├─ AppointmentJpaEntity + AppointmentPanacheRepository
│  ├─ SaleJpaEntity + SalePanacheRepository            │    │
│  ├─ ReportJpaEntity + ReportPanacheRepository        │    │
│  └─ AuditLogJpaEntity + AuditPanacheRepository       │    │
└──────────────────────────┬───────────────────────────────────┘
                           │
            Hibernate ORM + JDBC Drivers
                           │
         ┌──────────────────▼────────────────┐
         │      POSTGRESQL DATABASE          │
         │  ┌─────────────────────────────┐ │
         │  │ items table (24 rows)       │ │
         │  │ customers table             │ │
         │  │ employees table             │ │
         │  │ appointments table          │ │
         │  │ sales table                 │ │
         │  │ sale_items table            │ │
         │  │ inventory table             │ │
         │  │ daily_reports table         │ │
         │  │ audit_log table (JSON)      │ │
         │  └─────────────────────────────┘ │
         └─────────────────────────────────┘
```

---

## 🔄 Flujo de un Request Completo

### Escenario: Crear una Venta

```
1. CLIENT REQUEST
   ┌─────────────────────────────────────┐
   │ POST /api/sales                     │
   │ {                                   │
   │   "customerId": 1,                  │
   │   "employeeId": 2,                  │
   │   "paymentMethod": "CASH",          │
   │   "items": [                        │
   │     {"itemId": 10, "quantity": 1}   │
   │   ]                                 │
   │ }                                   │
   └──────────────┬──────────────────────┘
                  │
2. REST ADAPTER
   ┌──────────────▼──────────────────────┐
   │ SaleRestAdapter.createSale()        │
   │ - Valida RequestDTO                 │
   │ - Convierte a Sale domain           │
   │ - Llama SaleService.create()        │
   └──────────────┬──────────────────────┘
                  │
3. SERVICE LAYER
   ┌──────────────▼──────────────────────┐
   │ SaleService.create(Sale)            │
   │ - Valida customer existe            │
   │ - Valida employee existe            │
   │ - Valida items existen              │
   │ - Calcula total_amount              │
   │ - Crea SaleItems                    │
   │ - Llama SaleRepositoryPort.save()   │
   └──────────────┬──────────────────────┘
                  │
4. PERSISTENCE ADAPTER
   ┌──────────────▼──────────────────────┐
   │ SalePersistenceAdapter.save()       │
   │ - Convierte Sale → SaleJpaEntity    │
   │ - Convierte Items → SaleItemJpaEntity
   │ - Persiste en repository            │
   │ - @Transactional maneja todo        │
   └──────────────┬──────────────────────┘
                  │
5. PANACHE REPOSITORY
   ┌──────────────▼──────────────────────┐
   │ SalePanacheRepository               │
   │ - repository.persist(saleEntity)    │
   │ - repository.persist(itemEntities)  │
   └──────────────┬──────────────────────┘
                  │
6. HIBERNATE ORM + JDBC
   ┌──────────────▼──────────────────────┐
   │ - Mapea JpaEntity a SQL INSERT      │
   │ - Genera parámetros de consulta     │
   │ - Envía a PostgreSQL driver         │
   │ - Recibe ID generado (IDENTITY)    │
   └──────────────┬──────────────────────┘
                  │
7. DATABASE
   ┌──────────────▼──────────────────────┐
   │ INSERT INTO sales (...)             │
   │ INSERT INTO sale_items (...)        │
   │ COMMIT transaction                  │
   │ ← Devuelve IDs generados            │
   └──────────────┬──────────────────────┘
                  │
8. RETORNO
   ┌──────────────▼──────────────────────┐
   │ SalePersistenceAdapter.save()       │
   │ - JpaEntity con ID                  │
   │ - Convierte → Sale domain           │
   └──────────────┬──────────────────────┘
                  │
9. SERVICE RETORNA
   ┌──────────────▼──────────────────────┐
   │ SaleService.create() retorna        │
   │ Sale con ID generado                │
   └──────────────┬──────────────────────┘
                  │
10. REST ADAPTER RESPONDE
    ┌──────────────▼──────────────────────┐
    │ SaleRestAdapter                     │
    │ - Convierte Sale → SaleResponseDTO  │
    │ - HTTP 201 Created                  │
    │ {                                   │
    │   "id": 42,                         │
    │   "customerId": 1,                  │
    │   "employeeId": 2,                  │
    │   "totalAmount": 25.00,             │
    │   "paymentMethod": "CASH",          │
    │   "items": [...]                    │
    │ }                                   │
    └──────────────┬──────────────────────┘
                   │
11. AUDIT LOG
    ┌──────────────▼──────────────────────┐
    │ AuditService.record()               │
    │ - entityType: SALE                  │
    │ - entityId: 42                      │
    │ - action: CREATE                    │
    │ - newValues: {...}                  │
    │ - timestamp: now()                  │
    │ INSERT INTO audit_log               │
    └──────────────┬──────────────────────┘
                   │
12. CLIENT RESPONSE
    ┌──────────────▼──────────────────────┐
    │ HTTP 201 Created                    │
    │ {                                   │
    │   "id": 42,                         │
    │   "customerId": 1,                  │
    │   ...                               │
    │ }                                   │
    └─────────────────────────────────────┘
```

---

## 📦 Estructura de Módulo (Ejemplo: Item)

```
item/
│
├── domain/ (ENTIDADES DE DOMINIO)
│   └── Item.java
│       ├─ id: Integer
│       ├─ name: String
│       ├─ description: String
│       ├─ category: String (SERVICE|PRODUCT)
│       ├─ price: BigDecimal
│       ├─ isActive: Boolean
│       ├─ createdAt: LocalDateTime
│       └─ updatedAt: LocalDateTime
│
├── application/ (LÓGICA DE NEGOCIO)
│   ├── ItemService.java
│   │   ├─ create(Item): Item
│   │   ├─ update(Item): Item
│   │   ├─ delete(id): void
│   │   └─ Validaciones
│   ├── ItemFilterQuery.java
│   │   ├─ page, pageSize
│   │   ├─ search, category, active
│   │   ├─ sortBy, sortDirection
│   │   ├─ withDefaults()
│   │   └─ offset()
│   ├── ItemCommand.java (DTOs)
│   ├── PagedResponse.java (Paginación genérica)
│   └── port/
│       ├── in/ItemUseCase.java (interfaz)
│       └── out/ItemRepositoryPort.java (interfaz)
│
├── adapter/ (ADAPTADORES)
│   ├── in/rest/
│   │   ├── ItemRestAdapter.java
│   │   │   ├─ @Path("/items")
│   │   │   ├─ GET listItems()
│   │   │   ├─ POST createItem()
│   │   │   ├─ GET getItem()
│   │   │   ├─ PUT updateItem()
│   │   │   └─ Conversiones DTO ↔ Domain
│   │   └── DTOs
│   │       ├── ItemRequest.java
│   │       ├── ItemResponse.java
│   │       └── (generados por OpenAPI)
│   └── out/persistence/
│       ├── ItemJpaEntity.java (@Entity)
│       ├── ItemPanacheRepository.java
│       └── ItemPersistenceAdapter.java
│           ├─ find(ItemFilterQuery)
│           ├─ count(ItemFilterQuery)
│           ├─ save(Item)
│           ├─ findById(id)
│           ├─ delete(id)
│           ├─ toDomain()
│           └─ toPersistence()
```

---

## 🔐 Validación de Datos (Layers)

```
CLIENT REQUEST
    │
    ├─ HTTP Content-Type: application/json
    ├─ Parse JSON → RequestDTO
    │
REST ADAPTER
    ├─ @NotNull, @NotBlank
    ├─ @Min, @Max, @Size
    ├─ @Email, @Pattern
    │
APPLICATION SERVICE
    ├─ Validar reglas de negocio
    ├─ Validar referencias (FK)
    ├─ Validaciones de estado
    │
PERSISTENCE ADAPTER
    ├─ Convertir tipos
    ├─ @Column constraints
    │
DATABASE
    ├─ NOT NULL constraints
    ├─ UNIQUE constraints
    ├─ FK constraints
    ├─ CHECK constraints
    │
RESPONSE
    └─ 200 OK o Error
```

---

## 🗄️ Relaciones de Base de Datos

```
┌──────────────┐
│   customers  │◄──────┐
└──────────────┘       │
      ▲                │
      │                │
      │ customer_id    │ customer_id
      │                │
┌──────────────┐   ┌─────────────┐
│    sales     │   │appointments │
└──────────────┘   └─────────────┘
      │                │
      │ sale_id        │ employee_id
      │                │
      ▼                ▼
┌──────────────┐   ┌──────────────┐   ┌────────────┐
│  sale_items  │   │  employees   │   │   items    │
└──────────────┘   └──────────────┘   └────────────┘
      ▲                                      ▲
      │ item_id                              │
      └──────────────────────────────────────┘

┌──────────────────────────────┐
│ audit_log                    │
├──────────────────────────────┤
│ Registra cambios en todas    │
│ las tablas anteriores:       │
├──────────────────────────────┤
│ - ITEM                       │
│ - CUSTOMER                   │
│ - EMPLOYEE                   │
│ - SALE                       │
│ - APPOINTMENT                │
└──────────────────────────────┘

┌──────────────────────────────┐
│ daily_reports                │
├──────────────────────────────┤
│ Agregación diaria de:        │
│ - Total de ventas            │
│ - Transacciones por método   │
│ - Categorías de productos    │
│ - Top products               │
│ - Citas completadas          │
└──────────────────────────────┘
```

---

## 🚀 Flujo de Paginación

```
CLIENT REQUEST
│
├─ page=1, pageSize=10
│
REST ADAPTER
│
├─ Crear ItemFilterQuery
│ ├─ page: 1
│ ├─ pageSize: 10
│ ├─ search: null
│ └─ category: null
│
├─ Llamar itemRepository.find(query)
│
PERSISTENCE ADAPTER
│
├─ itemFilterQuery.withDefaults()
│ ├─ page >= 1 ✓
│ ├─ pageSize 1-100 ✓
│
├─ itemFilterQuery.offset() → (1-1) * 10 = 0
│
├─ Construir HQL:
│ SELECT item FROM ItemJpaEntity item
│ WHERE 1=1
│ ORDER BY item.id ASC
│ LIMIT 10 OFFSET 0
│
├─ Ejecutar count() para total:
│ SELECT COUNT(*) FROM ItemJpaEntity
│ → total = 24
│
├─ Calcular totalPages:
│ totalPages = ceil(24 / 10) = 3
│
├─ Calcular hasNextPage:
│ hasNextPage = (1 < 3) = true
│
RESPONSE
│
└─ PaginatedItems {
    data: [item1, item2, ...],
    pagination: {
      page: 1,
      pageSize: 10,
      total: 24,
      totalPages: 3,
      hasNextPage: true
    }
  }
```

---

## 🎯 Decisiones de Diseño

| Aspecto | Decisión | Razón |
|--------|----------|-------|
| **Arquitectura** | Hexagonal | Desacoplamiento y testabilidad |
| **ORM** | Panache | Simplicidad y productividad con Quarkus |
| **Paginación** | Limit + Offset | Compatible con SQL estándar |
| **DTOs** | Separados por módulo | Evolución independiente |
| **Enums** | En código | Type-safety y validación |
| **Auditoría** | JSON en BD | Flexibilidad en cambios |
| **Transacciones** | @Transactional | Control automático |
| **Inyección** | Quarkus CDI | Integración nativa |
| **Tests** | Unitarios sin servidor | Velocidad y aislamiento |
| **Documentación** | OpenAPI 3.0 | Estándar industria |

---

¡La arquitectura está optimizada para escalabilidad, mantenibilidad y rendimiento! 🚀
