# Referencia de la API

Base URL: `http://localhost:8080/api`

El contrato completo, incluidos schemas, parámetros requeridos y respuestas, está en
`src/main/resources/openapi.yaml` y en Swagger UI (`/q/swagger-ui`). Esta página resume
las rutas que implementan los adapters actuales.

## Rutas

| Módulo | Rutas |
| --- | --- |
| Items | `GET /items`, `POST /items`, `GET /items/{id}`, `PUT /items/{id}` |
| Customers | `GET /customers`, `POST /customers`, `GET /customers/{id}`, `PUT /customers/{id}`, `DELETE /customers/{id}` |
| Employees | `GET /employees`, `POST /employees`, `GET /employees/{id}`, `PUT /employees/{id}`, `DELETE /employees/{id}` |
| Appointments | `GET /appointments`, `POST /appointments`, `GET /appointments/{id}`, `PUT /appointments/{id}`, `DELETE /appointments/{id}` |
| Sales | `GET /sales`, `POST /sales`, `GET /sales/{id}` |
| Reports | `GET /reports/daily/{date}`, `POST /reports/daily/generate/{date}` |
| Audit logs | `GET /audit-logs` |

Son 25 operaciones: 4 + 5 + 5 + 5 + 3 + 2 + 1.

## Listados y paginación

Los listados reciben `page` y `pageSize`, con mínimo 1 y máximo 100. Si se omiten en la
implementación, los servicios usan `page=1` y `pageSize=10`. La forma de respuesta es:

```json
{
  "data": [],
  "pagination": {
    "page": 1,
    "pageSize": 10,
    "total": 0,
    "totalPages": 0,
    "hasNextPage": false
  }
}
```

Filtros disponibles:

| Listado | Filtros |
| --- | --- |
| Items | `search`, `category=SERVICE|PRODUCT`, `active`, `sortBy`, `sortDirection=asc|desc` |
| Customers | `search`, `page`, `pageSize` |
| Employees | `search`, `role=BARBER|STYLIST|RECEPTIONIST|MANAGER`, `active`, `page`, `pageSize` |
| Appointments | `startDate`, `endDate`, `employeeId`, `customerId`, `status`, `page`, `pageSize` |
| Sales | `page`, `pageSize` |
| Audit logs | `entityType`, `action=CREATE|UPDATE|DELETE`, `page`, `pageSize` |

Los estados válidos de una cita son `SCHEDULED`, `PENDING`, `IN_PROGRESS`, `COMPLETED` y
`CANCELLED`. La implementación asigna `PENDING` cuando la petición no proporciona estado.

## Payloads principales

### Item

```json
{
  "name": "Corte clásico",
  "description": "Servicio de barbería",
  "category": "SERVICE",
  "price": 50000,
  "active": true
}
```

### Customer

```json
{
  "name": "Juan Pérez",
  "phone": "987654321",
  "email": "juan@example.com",
  "address": "Av. Principal 123"
}
```

### Employee

```json
{
  "name": "Carlos López",
  "role": "BARBER",
  "phone": "987654321",
  "email": "carlos@barbershop.com",
  "active": true
}
```

### Appointment

```json
{
  "customerId": 1,
  "employeeId": 1,
  "scheduledAt": "2026-08-02T10:30:00Z",
  "notes": "Cliente nuevo",
  "status": "PENDING"
}
```

### Sale

```json
{
  "customerId": 1,
  "employeeId": 1,
  "paymentMethod": "CASH",
  "discount": 0,
  "notes": "Pago en caja",
  "items": [
    {
      "itemId": 1,
      "quantity": 1,
    }
  ]
}
```

Los métodos de pago son `CASH`, `TRANSFER` y `CARD`. El request de venta sólo recibe
`itemId` y `quantity` por línea. El backend obtiene el precio vigente del catálogo y calcula
subtotales y total con `BigDecimal`, redondeando a dos decimales con `HALF_UP`. Los campos
`unitPrice`, `subtotalAmount`, `totalAmount` y `soldAt` son únicamente de respuesta. Clientes
que enviaban `unitPrice` deben eliminarlo del payload; ya no forma parte del contrato.

## Reportes y auditoría

```powershell
curl http://localhost:8080/api/reports/daily/2026-08-01
curl -Method Post http://localhost:8080/api/reports/daily/generate/2026-08-01
curl "http://localhost:8080/api/audit-logs?page=1&pageSize=20&action=CREATE"
```

La generación de reportes crea un registro inicial si no existe; actualmente no agrega
ventas ni citas. La auditoría usa el usuario `system` porque todavía no hay autenticación.

## Códigos HTTP

| Código | Uso |
| --- | --- |
| `200` | Lectura o actualización correcta |
| `201` | Creación correcta |
| `204` | Eliminación correcta |
| `400` | Parámetros o payload inválido |
| `404` | Recurso inexistente |

Los errores de recursos inexistentes pueden devolver un cuerpo vacío, según el adapter.
