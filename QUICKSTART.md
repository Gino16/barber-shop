# Inicio rápido

## Requisitos

- Java 21
- Maven 3.8+ (o `mvnw.cmd`)
- Docker Desktop
- `curl`; `jq` es opcional para formatear JSON

## 1. Preparar PostgreSQL

Desde PowerShell, en la raíz del repositorio:

```powershell
Copy-Item devops\.env.example devops\.env
docker compose --env-file devops\.env -f devops\docker-compose.yml up -d
docker compose --env-file devops\.env -f devops\docker-compose.yml ps
```

El contenedor `barber-shop-postgres` ejecuta `database/DDL.sql` solo cuando crea el
volumen por primera vez. Los valores por defecto son:

```text
database: barber_shop
user:     barber_shop
password: barber_shop
port:     5432
```

Para una base existente, asigna precios reales a los ítems y ejecuta la migración dentro
de una transacción antes de usar ventas:

```powershell
Get-Content database\migrations\V2__item_price_and_money_constraints.sql |
  docker exec -i barber-shop-postgres psql -U barber_shop -d barber_shop
```

La migración falla deliberadamente si encuentra `items.price` nulo; no inventa precios ni
modifica las líneas históricas de `sale_items`.

Para cambiar estos valores, edita `devops\.env`. La aplicación también acepta
`POSTGRES_HOST`, `POSTGRES_PORT`, `POSTGRES_DB`, `POSTGRES_USER` y
`POSTGRES_PASSWORD`.

## 2. Compilar y ejecutar

```powershell
.\mvnw.cmd clean compile
.\mvnw.cmd quarkus:dev
```

La API queda disponible en `http://localhost:8080`. En otra terminal:

```powershell
curl http://localhost:8080/q/health
curl http://localhost:8080/q/openapi
```

La interfaz interactiva está en `http://localhost:8080/q/swagger-ui`.

## 3. Probar el flujo principal

El endpoint de items requiere paginación explícita en el contrato:

```powershell
curl "http://localhost:8080/api/items?page=1&pageSize=10"
```

Crear un item:

```powershell
curl -Method Post "http://localhost:8080/api/items" `
  -ContentType "application/json" `
  -Body '{"name":"Corte clásico","description":"Servicio de barbería","category":"SERVICE","price":50000,"active":true}'
```

Crear una venta:

```powershell
curl -Method Post "http://localhost:8080/api/sales" `
  -ContentType "application/json" `
  -Body '{"customerId":1,"employeeId":1,"paymentMethod":"CASH","discount":0,"items":[{"itemId":1,"quantity":1}]}'
```

El precio de la venta se obtiene del catálogo en el backend. `unitPrice`, subtotales y
totales son campos de respuesta; los clientes existentes deben dejar de enviarlos.

La referencia completa de rutas, filtros y payloads está en `ENDPOINTS.md`; el contrato
definitivo está en `src/main/resources/openapi.yaml`.

## Tests y empaquetado

```powershell
.\mvnw.cmd test
.\mvnw.cmd clean package
java -jar target\barber-shop-1.0.0-SNAPSHOT-runner.jar
```

Para el perfil nativo:

```powershell
.\mvnw.cmd clean package -Pnative
```

## Problemas frecuentes

**PostgreSQL no responde:** `docker compose --env-file devops\.env -f
devops\docker-compose.yml ps` y revisa los logs con `docker logs barber-shop-postgres`.

**El puerto 5432 está ocupado:** cambia `POSTGRES_PORT` en `devops\.env`; la URL de
la aplicación usa la misma variable.

**El esquema no refleja cambios:** el volumen conserva la base existente. En un entorno
local desechable, `docker compose ... down -v` elimina el volumen; luego vuelve a ejecutar
`up -d`. No uses ese comando si necesitas conservar datos.
