# 🏛️ Arquitectura del Barbershop API

## Visión General

El proyecto utiliza **arquitectura hexagonal (ports and adapters)** con **Quarkus** como framework principal.

```
┌─────────────────────────────────────────────────────────────┐
│                    REST ADAPTERS (in)                       │
│  ItemRestAdapter | CustomerRestAdapter | SaleRestAdapter   │
│EmployeeRestAdapter | AppointmentRestAdapter | ReportRestAdapter
└────────────────────────────┬────────────────────────────────┘
                             │
┌────────────────────────────▼────────────────────────────────┐
│              APPLICATION LAYER (Use Cases)                  │
│  ItemService | CustomerService | SaleService              │
│EmployeeService | AppointmentService | ReportService       │
└────────────────────────────┬────────────────────────────────┘
                             │
┌────────────────────────────▼────────────────────────────────┐
│                  PORT INTERFACES                            │
│  ItemRepositoryPort | CustomerRepositoryPort | SaleRepositoryPort
│  EmployeeRepositoryPort | AppointmentRepositoryPort
└────────────────────────────┬────────────────────────────────┘
                             │
┌────────────────────────────▼────────────────────────────────┐
│        PERSISTENCE ADAPTERS (out - JPA/Panache)            │
│  ItemPersistenceAdapter | CustomerPersistenceAdapter       │
│  SalePersistenceAdapter | EmployeePersistenceAdapter       │
│  AppointmentPersistenceAdapter | ReportPersistenceAdapter  │
└────────────────────────────┬────────────────────────────────┘
                             │
┌────────────────────────────▼────────────────────────────────┐
│              DOMAIN ENTITIES                                │
│  Item | Customer | Sale | Employee | Appointment | Report   │
│  DailyReport | AuditLog                                     │
└─────────────────────────────────────────────────────────────┘
```

## Módulos Principales

### 1. **ITEM** (Catálogo)
**Propósito:** Gestionar servicios y productos del barbershop

**Carpeta:** `src/main/java/org/barbershop/item/`

**Estructura:**
```
item/
├── domain/
│   └── Item.java
├── application/
│   ├── ItemService.java
│   ├── ItemFilterQuery.java
│   ├── ItemCommand.java
│   ├── PagedResponse.java
│   └── port/
│       ├── in/ItemUseCase.java
│       └── out/ItemRepositoryPort.java
├── adapter/
│   ├── in/rest/ItemRestAdapter.java
│   └── out/persistence/
│       ├── ItemJpaEntity.java
│       ├── ItemPanacheRepository.java
│       └── ItemPersistenceAdapter.java
```

**Endpoints:**
- `GET /api/items?page=1&pageSize=10` - Listar items
- `GET /api/items/{id}` - Obtener item
- `POST /api/items` - Crear item
- `PUT /api/items/{id}` - Actualizar item

**Filtros:**
- `search` - Búsqueda en nombre/descripción
- `category` - SERVICE, PRODUCT
- `active` - true, false
- `sortBy` - id, name, category, createdAt
- `sortDirection` - asc, desc

---

### 2. **CUSTOMER** (Clientes)
**Propósito:** Gestionar registro de clientes

**Carpeta:** `src/main/java/org/barbershop/customer/`

**Campos:**
- name, email, phone, address, notes, is_active, created_at, updated_at

**Endpoints:**
- `GET /api/customers` - Listar clientes
- `GET /api/customers/{id}` - Obtener cliente
- `POST /api/customers` - Crear cliente
- `PUT /api/customers/{id}` - Actualizar cliente
- `DELETE /api/customers/{id}` - Eliminar cliente

**Filtros:**
- `search` - En nombre/email
- `active` - true, false

---

### 3. **EMPLOYEE** (Empleados)
**Propósito:** Gestionar barberos y estilistas

**Carpeta:** `src/main/java/org/barbershop/employee/`

**Campos:**
- name, email, phone, role (BARBER, STYLIST, MANAGER, ADMIN), salary, is_active, hired_date

**Endpoints:**
- `GET /api/employees` - Listar empleados
- `GET /api/employees/{id}` - Obtener empleado
- `POST /api/employees` - Crear empleado
- `PUT /api/employees/{id}` - Actualizar empleado
- `DELETE /api/employees/{id}` - Eliminar empleado

**Filtros:**
- `search` - En nombre/email
- `role` - BARBER, STYLIST, MANAGER, ADMIN
- `active` - true, false

**Enums:**
```java
public enum EmployeeRole {
    BARBER,
    STYLIST,
    MANAGER,
    ADMIN
}
```

---

### 4. **APPOINTMENT** (Citas)
**Propósito:** Gestionar reservas de servicios

**Carpeta:** `src/main/java/org/barbershop/appointment/`

**Campos:**
- customer_id, employee_id, item_id, scheduled_at, completed_at, status, notes

**Endpoints:**
- `GET /api/appointments` - Listar citas
- `GET /api/appointments/{id}` - Obtener cita
- `POST /api/appointments` - Crear cita
- `PUT /api/appointments/{id}` - Actualizar cita
- `DELETE /api/appointments/{id}` - Eliminar cita

**Filtros:**
- `startDate`, `endDate` - Rango de fechas
- `employeeId`, `customerId` - Filtrar por empleado/cliente
- `status` - PENDING, COMPLETED, CANCELLED

**Enums:**
```java
public enum AppointmentStatus {
    PENDING,
    COMPLETED,
    CANCELLED
}
```

---

### 5. **SALE** (Ventas)
**Propósito:** Registrar transacciones de servicios y productos

**Carpeta:** `src/main/java/org/barbershop/sale/`

**Campos:**
- customer_id, employee_id, sold_at, total_amount, payment_method, discount, notes

**Endpoints:**
- `GET /api/sales` - Listar ventas
- `GET /api/sales/{id}` - Obtener venta con items
- `POST /api/sales` - Crear venta (completa con items)

**Estructura de Venta:**
```json
{
  "customerId": 1,
  "employeeId": 2,
  "paymentMethod": "CASH",
  "discount": 5.00,
  "items": [
    {
      "itemId": 10,
      "quantity": 1,
      "unitPrice": 25.00
    },
    {
      "itemId": 15,
      "quantity": 2,
      "unitPrice": 10.00
    }
  ]
}
```

**Enums:**
```java
public enum PaymentMethod {
    CASH,
    TRANSFER,
    CARD
}
```

---

### 6. **REPORT** (Reportería)
**Propósito:** Análisis diario de operaciones y ventas

**Carpeta:** `src/main/java/org/barbershop/report/`

**Campos:**
- report_date, total_sales, total_transactions, cash_sales, card_sales, transfer_sales, services_sales, products_sales, total_customers, new_customers, average_transaction, top_product_id, top_product_name, top_product_quantity, total_appointments, completed_appointments

**Endpoints:**
- `GET /api/reports/daily/{date}` - Obtener reporte (YYYY-MM-DD)
- `POST /api/reports/daily/generate/{date}` - Generar reporte

**Respuesta:**
```json
{
  "reportDate": "2024-07-23",
  "totalSales": 250.50,
  "totalTransactions": 12,
  "cashSales": 180.00,
  "cardSales": 70.50,
  "transferSales": 0.00,
  "servicesSales": 150.00,
  "productsSales": 100.50,
  "totalCustomers": 8,
  "newCustomers": 2,
  "averageTransaction": 20.87,
  "topProduct": { "id": 15, "name": "Pomada Fuerte", "quantity": 5 },
  "totalAppointments": 12,
  "completedAppointments": 11,
  "createdAt": "2024-07-23T17:00:00"
}
```

---

### 7. **AUDIT** (Auditoría)
**Propósito:** Trazabilidad de cambios en el sistema

**Carpeta:** `src/main/java/org/barbershop/audit/`

**Campos:**
- entity_type, entity_id, action, old_values (JSON), new_values (JSON), user_name, timestamp

**Endpoints:**
- `GET /api/audit-logs` - Listar cambios con paginación

**Ejemplo de Entrada:**
```json
{
  "entityType": "ITEM",
  "entityId": 5,
  "action": "UPDATE",
  "oldValues": {
    "name": "Corte Fade",
    "price": 25.00
  },
  "newValues": {
    "name": "Corte Fade Premium",
    "price": 30.00
  },
  "userName": "admin"
}
```

**Enums:**
```java
public enum AuditAction {
    CREATE,
    UPDATE,
    DELETE
}

public enum EntityType {
    ITEM,
    CUSTOMER,
    EMPLOYEE,
    SALE,
    APPOINTMENT
}
```

---

## Patrones de Diseño

### 1. Paginación Estándar

Todas las consultas GET devuelven estructura paginada:

```json
{
  "data": [
    { "id": 1, "name": "Item 1", ... },
    { "id": 2, "name": "Item 2", ... }
  ],
  "pagination": {
    "page": 1,
    "pageSize": 10,
    "total": 100,
    "totalPages": 10,
    "hasNextPage": true
  }
}
```

### 2. Filter Query Pattern

Cada módulo tiene un `FilterQuery`:

```java
public class ItemFilterQuery {
    private Integer page;
    private Integer pageSize;
    private String search;
    private String category;
    private Boolean active;
    private String sortBy;
    private String sortDirection;
    
    public ItemFilterQuery withDefaults() {
        // Aplicar valores por defecto y validaciones
    }
    
    public int offset() {
        return (page - 1) * pageSize;
    }
}
```

### 3. Service Layer

Los servicios contienen lógica de negocio:

```java
@ApplicationScoped
@Transactional
public class ItemService implements ItemUseCase {
    @Inject
    ItemRepositoryPort itemRepository;
    
    public Item create(Item item) {
        // Validar
        // Calcular
        return itemRepository.save(item);
    }
}
```

### 4. Persistence Adapter

Los adapters implementan interfaces y manejan JPA:

```java
@ApplicationScoped
@Transactional
public class ItemPersistenceAdapter implements ItemRepositoryPort {
    @Inject
    ItemPanacheRepository repository;
    
    public PagedResponse<Item> find(ItemFilterQuery query) {
        // Construir HQL dinámico
        // Aplicar filtros, búsqueda, ordenamiento
        // Retornar PagedResponse
    }
}
```

---

## Base de Datos

### Tablas Principales

| Tabla | Propósito | Registros |
|-------|-----------|-----------|
| `items` | Servicios y productos | ~24 |
| `customers` | Clientes | Variable |
| `employees` | Empleados | ~5-10 |
| `appointments` | Citas programadas | Variable |
| `sales` | Transacciones diarias | Variable |
| `sale_items` | Detalles de ventas | Variable |
| `inventory` | Stock de productos | ~10 |
| `daily_reports` | Reportes diarios | ~365 |
| `audit_log` | Historial de cambios | Variable |

### Índices Creados

```sql
CREATE INDEX idx_items_category ON items (category);
CREATE INDEX idx_items_is_active ON items (is_active);
CREATE INDEX idx_sales_sold_at ON sales (sold_at);
CREATE INDEX idx_audit_log_timestamp ON audit_log (timestamp);
-- y más...
```

---

## Tecnologías Utilizadas

| Componente | Tecnología | Versión |
|-----------|-----------|---------|
| Framework | Quarkus | 3.37.3 |
| ORM | Hibernate + Panache | 6.6.x |
| Base de Datos | PostgreSQL | 15+ |
| Build | Maven | 3.8+ |
| Generación API | OpenAPI Generator | 7.10.0 |
| Logging | Log4j 2 | 2.21.x |
| Testing | JUnit 5 | 5.10.x |
| Mocking | Mockito | 5.x |

---

## Flujo de Solicitud

```
1. Cliente HTTP
   ↓
2. REST Adapter (ItemRestAdapter)
   - Convierte RequestDTO → Domain Model
   - Llama a Service
   ↓
3. Service (ItemService)
   - Lógica de negocio
   - Validaciones
   - Llama a Repository Port
   ↓
4. Repository Port Interface (ItemRepositoryPort)
   - Contrato de persistencia
   ↓
5. Persistence Adapter (ItemPersistenceAdapter)
   - Implementa Repository Port
   - Construye queries HQL
   - Usa Panache Repository
   ↓
6. Panache Repository (ItemPanacheRepository)
   - Ejecuta en PostgreSQL
   - Retorna JpaEntity
   ↓
7. Persistence Adapter
   - Convierte JpaEntity → Domain Model
   - Retorna Domain Model
   ↓
8. Service
   - Retorna Domain Model
   ↓
9. REST Adapter
   - Convierte Domain Model → ResponseDTO
   - Retorna Response HTTP
   ↓
10. Cliente HTTP
```

---

## Ejemplo Completo: Crear una Venta

### Request
```bash
curl -X POST "http://localhost:8080/api/sales?page=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 5,
    "employeeId": 2,
    "paymentMethod": "CASH",
    "discount": 0,
    "items": [
      { "itemId": 1, "quantity": 1, "unitPrice": 25.00 },
      { "itemId": 15, "quantity": 1, "unitPrice": 10.00 }
    ]
  }'
```

### Flujo Interno
1. **SaleRestAdapter.createSale()** recibe SaleRequest
2. Convierte SaleRequest → Sale + List<SaleItem>
3. Llama **SaleService.create(sale)**
4. Service valida: customer existe, items existen, etc.
5. Service calcula: total_amount = 25 + 10 = 35
6. Llama **SaleRepositoryPort.save(sale)**
7. **SalePersistenceAdapter** convierte Sale → SaleJpaEntity
8. Persiste en PostgreSQL mediante Panache
9. Retorna Sale domain
10. Service retorna Sale
11. **SaleRestAdapter** convierte Sale → SaleResponseDTO
12. Retorna HTTP 201 Created

### Response
```json
{
  "id": 42,
  "customerId": 5,
  "employeeId": 2,
  "soldAt": "2024-07-23T17:25:00",
  "totalAmount": 35.00,
  "paymentMethod": "CASH",
  "discount": 0.00,
  "items": [
    { "itemId": 1, "itemName": "Corte Colegial", "quantity": 1, "unitPrice": 25.00 },
    { "itemId": 15, "itemName": "Pomada Fuerte", "quantity": 1, "unitPrice": 10.00 }
  ],
  "createdAt": "2024-07-23T17:25:00"
}
```

---

## Próximos Pasos

1. ✅ Crear tablas en PostgreSQL (ejecutar DDL.sql)
2. ✅ Cargar datos iniciales (ejecutar scripts/seed-items.sql)
3. 🔄 Implementar endpoints de reportería
4. 🔄 Agregar validaciones más robustas
5. 🔄 Integración con autenticación/autorización
6. 🔄 Tests de integración
7. 🔄 Deployment a producción

---

## Referencias

- OpenAPI Spec: `src/main/resources/openapi.yaml`
- README: `README.md`
- Tests: `TESTS.md`
- Scripts de carga: `scripts/`
