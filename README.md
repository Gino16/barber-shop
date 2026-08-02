# Barber Shop API

API REST para gestionar el catálogo, clientes, personal, citas, ventas, reportes y
auditoría de una barbería. Está construida con Java 21, Quarkus y PostgreSQL siguiendo
arquitectura hexagonal.

## Empezar

La guía reproducible de instalación está en [`QUICKSTART.md`](QUICKSTART.md):

```powershell
Copy-Item devops\.env.example devops\.env
docker compose --env-file devops\.env -f devops\docker-compose.yml up -d
.\mvnw.cmd quarkus:dev
```

- API: `http://localhost:8080/api`
- OpenAPI: `http://localhost:8080/q/openapi`
- Swagger UI: `http://localhost:8080/q/swagger-ui`
- Health: `http://localhost:8080/q/health`

## Documentación del repositorio

| Necesidad | Documento |
| --- | --- |
| Ejecutar, probar o resolver problemas | [`QUICKSTART.md`](QUICKSTART.md) |
| Entender capas, dependencias y flujo | [`ARCHITECTURE.md`](ARCHITECTURE.md) |
| Consultar rutas y payloads | [`ENDPOINTS.md`](ENDPOINTS.md) |
| Cargar catálogo de demostración | [`scripts/README.md`](scripts/README.md) |
| Contrato HTTP exacto | `src/main/resources/openapi.yaml` |
| Esquema de PostgreSQL | `database/DDL.sql` |

La documentación descriptiva se mantiene deliberadamente corta. No hay documentos de
estado histórico separados: el código y el OpenAPI son la fuente de verdad.

## Módulos y capacidades

- **Items:** servicios/productos, búsqueda, filtros, ordenamiento y paginación.
- **Customers:** alta, consulta, actualización y eliminación.
- **Employees:** personal filtrable por rol y estado.
- **Appointments:** agenda filtrable por fechas, cliente, empleado y estado.
- **Sales:** venta con múltiples items, descuento y forma de pago.
- **Reports:** consulta y generación de un reporte por fecha.
- **Audit:** listado paginado y registro de cambios de las operaciones implementadas.

Todos los listados comparten `page`, `pageSize` y metadatos `pagination`; revisa
`ENDPOINTS.md` para los filtros propios de cada módulo.

## Estructura

```text
src/main/java/org/barbershop/
├── item, customer, employee, appointment, sale, report, audit/
│   ├── domain/
│   ├── application/
│   │   └── port/{in,out}/
│   └── adapter/{in/rest,out/persistence}/
└── common/{pagination,filter}/
src/main/resources/openapi.yaml
database/DDL.sql
devops/docker-compose.yml
scripts/
```

El contrato OpenAPI genera modelos e interfaces en `target/`; los adapters de `src/main`
los implementan. No edites archivos generados.

## Desarrollo

```powershell
.\mvnw.cmd test
.\mvnw.cmd clean package
```

La aplicación no configura autenticación ni autorización. La tabla `inventory` existe en
el DDL, pero no hay todavía API de inventario. Del mismo modo, el reporte diario se crea
con valores iniciales y aún no calcula agregados desde las ventas.
