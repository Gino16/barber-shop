# Arquitectura

## Alcance actual

Barber Shop es una API REST en Java 21 y Quarkus 3.37.3. El código está organizado por
dominio y usa ports and adapters (arquitectura hexagonal). Los módulos actuales son:

| Módulo | Responsabilidad |
| --- | --- |
| `item` | Catálogo de servicios y productos |
| `customer` | Clientes |
| `employee` | Personal y roles |
| `appointment` | Citas y sus estados |
| `sale` | Ventas con detalle de items |
| `report` | Consulta y generación de reportes diarios |
| `audit` | Consulta y registro de cambios |
| `common` | Paginación y trazabilidad HTTP |

El contrato HTTP se mantiene en `src/main/resources/openapi.yaml` y las interfaces JAX-RS
se generan durante `generate-sources`. Los adapters REST implementan esas interfaces:
no se deben editar las clases generadas en `target/`.

## Flujo de una petición

```text
Cliente HTTP
    |
    v
OpenAPI generado -> adapter/in/rest
    |                 (DTO HTTP <-> objeto de aplicación/dominio)
    v
Caso de uso (port/in) -> Service
    |
    v
Puerto de salida (port/out)
    |
    v
adapter/out/persistence -> PanacheRepository -> PostgreSQL
```

1. Quarkus recibe la petición bajo `/api`.
2. El adapter REST recibe el DTO generado por OpenAPI y crea un command o query.
3. El servicio aplica defaults, reglas de negocio y paginación.
4. El servicio llama al puerto de persistencia, sin depender de JPA directamente.
5. El adapter de persistencia construye la consulta, convierte entre dominio y entidad JPA
   y usa Panache dentro de una transacción cuando modifica datos.
6. El adapter REST transforma el resultado a la respuesta OpenAPI y devuelve el código HTTP.

Las búsquedas paginadas devuelven:

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

`common/pagination/PagedResponse` centraliza esos metadatos. Los valores por defecto son
`page=1` y `pageSize=10`; el tamaño máximo es 100. Los filtros concretos están definidos
en cada `*FilterQuery`.

## Capas y estructura

Cada módulo sigue este patrón:

```text
<module>/
├── domain/                         Records y enums del negocio
├── application/
│   ├── *Command.java               Entrada para crear o actualizar
│   ├── *FilterQuery.java           Filtros y paginación
│   ├── *Service.java               Casos de uso
│   └── port/
│       ├── in/*UseCase.java        Puerto de entrada
│       └── out/*RepositoryPort.java Puerto de salida
└── adapter/
    ├── in/rest/*RestAdapter.java   Adapter HTTP
    └── out/persistence/            Entidades y repositorio Panache
```

Los objetos de dominio son records y no son las entidades JPA. Esta separación permite
probar servicios y adapters sin acoplar el caso de uso al esquema de PostgreSQL.

## Reglas relevantes del flujo

- **Items:** se filtran por texto, categoría y estado activo; el texto busca en nombre y
  descripción.
- **Ventas:** `SaleService` calcula cada subtotal y obtiene el total como subtotal menos
  descuento. Después de guardar registra un evento `CREATE`.
- **Auditoría:** items, clientes, empleados, citas y ventas llaman a `AuditLogger` para
  registrar altas, cambios y bajas que implementan. El usuario actual del logger es
  actualmente `system`; no hay autenticación.
- **Reportes:** `ReportService` consulta el reporte por fecha y, si no existe, crea un
  reporte inicial con valores en cero. No existe todavía un agregador que calcule los
  totales a partir de ventas y citas.
- **Trazas:** `TraceIdResponseFilter` añade el identificador de traza a las respuestas; el
  formato de log lo incluye para correlacionar peticiones.

## Persistencia

`database/DDL.sql` define nueve tablas:

`items`, `customers`, `employees`, `appointments`, `sales`, `sale_items`, `inventory`,
`audit_log` y `daily_reports`.

El esquema se crea al iniciar PostgreSQL mediante `devops/docker-compose.yml`. Hibernate
está configurado con `schema-management.strategy: none`, por lo que los cambios de
esquema deben hacerse en el DDL o en una migración explícita, no mediante `update`.
Aunque existe la tabla `inventory`, todavía no hay módulo ni endpoints de inventario.

## Decisiones y límites actuales

- Quarkus REST, Jackson, Hibernate ORM Panache, PostgreSQL y CDI.
- OpenAPI 3.0.3 como contrato de entrada.
- Paginación offset/limit, con máximo de 100 elementos.
- Validación declarativa disponible mediante Hibernate Validator y validación de enums en
  los adapters; el manejo de errores aún es básico (por ejemplo, 404 vacío).
- No hay autenticación, autorización, migraciones, integración de pagos ni cálculo
  completo de inventario.

## Añadir un módulo o endpoint

1. Actualiza `src/main/resources/openapi.yaml`.
2. Ejecuta Maven para generar las interfaces y modelos.
3. Implementa dominio, puertos, servicio y adapters siguiendo un módulo existente.
4. Añade o ajusta el esquema en `database/DDL.sql`.
5. Añade pruebas bajo `src/test/java`.
6. Actualiza `ENDPOINTS.md` solo con el contrato que realmente expone OpenAPI.
