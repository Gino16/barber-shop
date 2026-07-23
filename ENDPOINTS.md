# 📊 ENDPOINTS REFERENCE

## 📋 Índice Rápido

| Módulo           | Endpoints        | Método                 |
|------------------|------------------|------------------------|
| **Items**        | 4                | GET, POST, PUT, DELETE |
| **Customers**    | 5                | GET, POST, PUT, DELETE |
| **Employees**    | 5                | GET, POST, PUT, DELETE |
| **Appointments** | 5                | GET, POST, PUT, DELETE |
| **Sales**        | 3                | GET, POST              |
| **Reports**      | 2                | GET, POST              |
| **AuditLogs**    | 1                | GET                    |
| **TOTAL**        | **25 Endpoints** |                        |

---

## 🏷️ ITEMS (Catálogo de Servicios y Productos)

### GET /api/items

**Listar items con paginación y filtros**

```bash
curl -s "http://localhost:8080/api/items?page=1&pageSize=10" | jq
```

**Parámetros Query:**
| Parámetro | Tipo | Requerido | Descripción | |-----------|------|----------|-------------| |
page | integer | ✅ | Número de página (mín: 1) | | pageSize | integer | ✅ | Items por página (máx:
100) | | search | string | ❌ | Buscar en nombre/descripción | | category | string | ❌ | SERVICE o
PRODUCT | | active | boolean | ❌ | true o false | | sortBy | string | ❌ | id, name, category,
createdAt | | sortDirection | string | ❌ | asc o desc |

**Response (200 OK):**

```json
{
  "data": [
    {
      "id": 1,
      "name": "Corte Colegial",
      "description": "Corte tradicional...",
      "category": "SERVICE",
      "price": 25.00,
      "isActive": true,
      "createdAt": "2024-07-23T10:00:00",
      "updatedAt": "2024-07-23T10:00:00"
    }
  ],
  "pagination": {
    "page": 1,
    "pageSize": 10,
    "total": 24,
    "totalPages": 3,
    "hasNextPage": true
  }
}
```

---

### POST /api/items

**Crear nuevo item**

```bash
curl -X POST "http://localhost:8080/api/items?page=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Corte Premium",
    "description": "Corte con masaje",
    "category": "SERVICE",
    "price": 35.00,
    "isActive": true
  }' | jq
```

**Request Body:**

```json
{
  "name": "string (required, max 100)",
  "description": "string",
  "category": "SERVICE o PRODUCT (required)",
  "price": "number (required)",
  "isActive": "boolean (required)"
}
```

**Response (201 Created):**

```json
{
  "id": 25,
  "name": "Corte Premium",
  "description": "Corte con masaje",
  "category": "SERVICE",
  "price": 35.00,
  "isActive": true,
  "createdAt": "2024-07-23T17:30:00",
  "updatedAt": "2024-07-23T17:30:00"
}
```

---

### GET /api/items/{id}

**Obtener un item específico**

```bash
curl -s "http://localhost:8080/api/items/1" | jq
```

**Response (200 OK):**

```json
{
  "id": 1,
  "name": "Corte Colegial",
  "description": "Corte tradicional de colegial...",
  "category": "SERVICE",
  "price": 25.00,
  "isActive": true,
  "createdAt": "2024-07-23T10:00:00",
  "updatedAt": "2024-07-23T10:00:00"
}
```

---

### PUT /api/items/{id}

**Actualizar un item**

```bash
curl -X PUT "http://localhost:8080/api/items/1" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Corte Colegial Premium",
    "description": "Actualizado",
    "category": "SERVICE",
    "price": 30.00,
    "isActive": true
  }' | jq
```

**Response (200 OK):**

```json
{
  "id": 1,
  "name": "Corte Colegial Premium",
  "description": "Actualizado",
  "category": "SERVICE",
  "price": 30.00,
  "isActive": true,
  "createdAt": "2024-07-23T10:00:00",
  "updatedAt": "2024-07-23T17:30:00"
}
```

---

## 👥 CUSTOMERS (Clientes)

### GET /api/customers

**Listar clientes**

```bash
curl -s "http://localhost:8080/api/customers?page=1&pageSize=10&search=juan" | jq
```

**Parámetros:** page, pageSize, search, active, sortBy, sortDirection

---

### POST /api/customers

**Crear cliente**

```bash
curl -X POST "http://localhost:8080/api/customers?page=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Juan Pérez",
    "email": "juan@example.com",
    "phone": "987654321",
    "address": "Av. Principal 123",
    "notes": "Cliente VIP",
    "isActive": true
  }' | jq
```

**Request Body:**

```json
{
  "name": "string (required, max 100)",
  "email": "string (max 100)",
  "phone": "string (max 20)",
  "address": "string",
  "notes": "string",
  "isActive": "boolean (required)"
}
```

**Response (201 Created):**

```json
{
  "id": 1,
  "name": "Juan Pérez",
  "email": "juan@example.com",
  "phone": "987654321",
  "address": "Av. Principal 123",
  "notes": "Cliente VIP",
  "isActive": true,
  "createdAt": "2024-07-23T17:35:00",
  "updatedAt": "2024-07-23T17:35:00"
}
```

---

### GET /api/customers/{id}

**Obtener cliente**

```bash
curl -s "http://localhost:8080/api/customers/1" | jq
```

---

### PUT /api/customers/{id}

**Actualizar cliente**

```bash
curl -X PUT "http://localhost:8080/api/customers/1" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Juan Pérez García",
    "email": "juanpg@example.com",
    "phone": "987654322",
    "isActive": true
  }' | jq
```

---

### DELETE /api/customers/{id}

**Eliminar cliente**

```bash
curl -X DELETE "http://localhost:8080/api/customers/1" | jq
```

**Response (204 No Content)**

---

## 👔 EMPLOYEES (Empleados)

### GET /api/employees

**Listar empleados**

```bash
curl -s "http://localhost:8080/api/employees?page=1&pageSize=10&role=BARBER" | jq
```

**Filtros:** search, role (BARBER, STYLIST, MANAGER, ADMIN), active

---

### POST /api/employees

**Crear empleado**

```bash
curl -X POST "http://localhost:8080/api/employees?page=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Carlos López",
    "email": "carlos@barbershop.com",
    "phone": "987654321",
    "role": "BARBER",
    "salary": 1500.00,
    "hiredDate": "2024-01-15",
    "isActive": true
  }' | jq
```

**Request Body:**

```json
{
  "name": "string (required)",
  "email": "string",
  "phone": "string",
  "role": "BARBER|STYLIST|MANAGER|ADMIN (required)",
  "salary": "number",
  "hiredDate": "date (YYYY-MM-DD)",
  "isActive": "boolean (required)"
}
```

---

### GET /api/employees/{id}

### PUT /api/employees/{id}

### DELETE /api/employees/{id}

Misma estructura que Customers

---

## 📅 APPOINTMENTS (Citas)

### GET /api/appointments

**Listar citas**

```bash
curl -s "http://localhost:8080/api/appointments?page=1&pageSize=10&status=PENDING" | jq
```

**Filtros:**

- startDate (YYYY-MM-DD)
- endDate (YYYY-MM-DD)
- employeeId
- customerId
- status (PENDING, COMPLETED, CANCELLED)

---

### POST /api/appointments

**Crear cita**

```bash
curl -X POST "http://localhost:8080/api/appointments?page=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "employeeId": 1,
    "itemId": 1,
    "scheduledAt": "2024-07-24T10:30:00",
    "status": "PENDING",
    "notes": "Cliente nuevo"
  }' | jq
```

**Request Body:**

```json
{
  "customerId": "integer (required)",
  "employeeId": "integer (required)",
  "itemId": "integer (required)",
  "scheduledAt": "datetime (required, ISO8601)",
  "status": "PENDING|COMPLETED|CANCELLED (required)",
  "notes": "string"
}
```

---

### PUT /api/appointments/{id}

**Actualizar cita (completar o cancelar)**

```bash
curl -X PUT "http://localhost:8080/api/appointments/1" \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "employeeId": 1,
    "itemId": 1,
    "scheduledAt": "2024-07-24T10:30:00",
    "completedAt": "2024-07-24T10:45:00",
    "status": "COMPLETED",
    "notes": "Completada"
  }' | jq
```

---

### DELETE /api/appointments/{id}

**Cancelar cita**

```bash
curl -X DELETE "http://localhost:8080/api/appointments/1" | jq
```

---

## 💳 SALES (Ventas)

### GET /api/sales

**Listar ventas**

```bash
curl -s "http://localhost:8080/api/sales?page=1&pageSize=10" | jq
```

**Filtros:** page, pageSize

---

### POST /api/sales

**Crear venta completa (con múltiples items)**

```bash
curl -X POST "http://localhost:8080/api/sales?page=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "employeeId": 1,
    "paymentMethod": "CASH",
    "discount": 5.00,
    "items": [
      {
        "itemId": 1,
        "quantity": 1,
        "unitPrice": 25.00
      },
      {
        "itemId": 15,
        "quantity": 2,
        "unitPrice": 10.00
      }
    ]
  }' | jq
```

**Request Body:**

```json
{
  "customerId": "integer (required)",
  "employeeId": "integer (required)",
  "paymentMethod": "CASH|TRANSFER|CARD (required)",
  "discount": "number (default: 0)",
  "items": [
    {
      "itemId": "integer (required)",
      "quantity": "integer (required)",
      "unitPrice": "number (required)"
    }
  ]
}
```

**Response (201 Created):**

```json
{
  "id": 42,
  "customerId": 1,
  "employeeId": 1,
  "soldAt": "2024-07-23T17:40:00",
  "totalAmount": 45.00,
  "paymentMethod": "CASH",
  "discount": 5.00,
  "items": [
    {
      "itemId": 1,
      "itemName": "Corte Colegial",
      "quantity": 1,
      "unitPrice": 25.00,
      "subtotalAmount": 25.00
    },
    {
      "itemId": 15,
      "itemName": "Pomada Fuerte",
      "quantity": 2,
      "unitPrice": 10.00,
      "subtotalAmount": 20.00
    }
  ],
  "createdAt": "2024-07-23T17:40:00"
}
```

---

### GET /api/sales/{id}

**Obtener venta con detalles**

```bash
curl -s "http://localhost:8080/api/sales/42" | jq
```

Retorna la misma estructura que POST response

---

## 📊 REPORTS (Reportería)

### GET /api/reports/daily/{date}

**Obtener reporte de un día**

```bash
curl -s "http://localhost:8080/api/reports/daily/2024-07-23" | jq
```

**Response (200 OK):**

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
  "topProduct": {
    "id": 15,
    "name": "Pomada Fuerte",
    "quantity": 5
  },
  "totalAppointments": 12,
  "completedAppointments": 11,
  "createdAt": "2024-07-23T23:59:59"
}
```

---

### POST /api/reports/daily/generate/{date}

**Generar reporte del día (calcula totales)**

```bash
curl -X POST "http://localhost:8080/api/reports/daily/generate/2024-07-23" | jq
```

**Response (201 Created):** Misma estructura que GET

---

## 📝 AUDIT LOGS (Auditoría)

### GET /api/audit-logs

**Listar cambios realizados**

```bash
curl -s "http://localhost:8080/api/audit-logs?page=1&pageSize=10&action=UPDATE" | jq
```

**Filtros:**

- page, pageSize
- entityType (ITEM, CUSTOMER, EMPLOYEE, SALE, APPOINTMENT)
- action (CREATE, UPDATE, DELETE)

**Response (200 OK):**

```json
{
  "data": [
    {
      "id": 1,
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
      "userName": "admin",
      "ipAddress": "127.0.0.1",
      "timestamp": "2024-07-23T17:30:00"
    }
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

---

## 🔍 Filtros Comunes

### Búsqueda por nombre

```bash
curl -s "http://localhost:8080/api/items?page=1&pageSize=10&search=fade" | jq
curl -s "http://localhost:8080/api/customers?page=1&pageSize=10&search=juan" | jq
```

### Filtrar por estado activo

```bash
curl -s "http://localhost:8080/api/items?page=1&pageSize=10&active=true" | jq
curl -s "http://localhost:8080/api/employees?page=1&pageSize=10&active=false" | jq
```

### Ordenamiento

```bash
curl -s "http://localhost:8080/api/items?page=1&pageSize=10&sortBy=name&sortDirection=desc" | jq
```

### Paginación

```bash
# Página 2
curl -s "http://localhost:8080/api/items?page=2&pageSize=10" | jq

# 50 items por página
curl -s "http://localhost:8080/api/items?page=1&pageSize=50" | jq

# Última página automáticamente disponible (hasNextPage=false)
```

---

## ✅ Códigos HTTP

| Código  | Significado  | Caso                           |
|---------|--------------|--------------------------------|
| **200** | OK           | GET exitoso, PUT exitoso       |
| **201** | Created      | POST exitoso, recurso creado   |
| **204** | No Content   | DELETE exitoso                 |
| **400** | Bad Request  | Datos inválidos                |
| **404** | Not Found    | Recurso no existe              |
| **409** | Conflict     | Violación de restricción única |
| **500** | Server Error | Error interno del servidor     |

---

## 🧪 Ejemplos cURL completos

### 1. Crear cliente, cita y venta en 3 pasos

```bash
# Paso 1: Crear cliente
CUSTOMER=$(curl -s -X POST "http://localhost:8080/api/customers?page=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -d '{"name":"Pedro","email":"pedro@test.com","phone":"123456789","isActive":true}' | jq -r '.id')

echo "Cliente creado: $CUSTOMER"

# Paso 2: Crear cita
APPOINTMENT=$(curl -s -X POST "http://localhost:8080/api/appointments?page=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -d "{\"customerId\":$CUSTOMER,\"employeeId\":1,\"itemId\":1,\"scheduledAt\":\"2024-07-24T10:30:00\",\"status\":\"PENDING\"}" | jq -r '.id')

echo "Cita creada: $APPOINTMENT"

# Paso 3: Registrar venta
SALE=$(curl -s -X POST "http://localhost:8080/api/sales?page=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -d "{\"customerId\":$CUSTOMER,\"employeeId\":1,\"paymentMethod\":\"CASH\",\"items\":[{\"itemId\":1,\"quantity\":1,\"unitPrice\":25}]}" | jq -r '.id')

echo "Venta registrada: $SALE"

# Obtener reporte del día
curl -s "http://localhost:8080/api/reports/daily/$(date +%Y-%m-%d)" | jq
```

---

¡Todos los endpoints están listos para usar! 🚀
