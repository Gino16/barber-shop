# 🚀 GUÍA RÁPIDA DE EJECUCIÓN

## 1️⃣ Prerequisitos

```bash
# Verificar Java 21
java -version
# openjdk version "21" (your.version.here)

# Verificar Maven
mvn -version
# Apache Maven 3.8+ 

# PostgreSQL corriendo
psql --version
# psql (PostgreSQL) 15+
```

---

## 2️⃣ Configurar Base de Datos

### Opción A: Con Docker

```bash
# Iniciar PostgreSQL en Docker
docker run --name barbershop-db \
  -e POSTGRES_USER=barber_shop \
  -e POSTGRES_PASSWORD=barbershop_123 \
  -e POSTGRES_DB=barber_shop \
  -p 5432:5432 \
  -d postgres:15-alpine

# Crear tablas
docker exec -i barbershop-db psql -U barber_shop -d barber_shop < database/DDL.sql

# Cargar datos de ejemplo
docker exec -i barbershop-db psql -U barber_shop -d barber_shop < scripts/seed-items.sql
```

### Opción B: Con PostgreSQL local

```bash
# Crear base de datos y usuario
createdb -U postgres barber_shop
psql -U postgres -d barber_shop -c "CREATE USER barber_shop WITH PASSWORD 'barbershop_123';"
psql -U postgres -d barber_shop -c "GRANT ALL PRIVILEGES ON DATABASE barber_shop TO barber_shop;"

# Crear tablas
psql -U barber_shop -d barber_shop -f database/DDL.sql

# Cargar datos
psql -U barber_shop -d barber_shop -f scripts/seed-items.sql
```

---

## 3️⃣ Actualizar Configuración (si es necesaria)

Editar `src/main/resources/application.yaml`:

```yaml
quarkus:
  datasource:
    db-kind: postgresql
    username: barber_shop
    password: barbershop_123
    jdbc:
      url: jdbc:postgresql://localhost:5432/barber_shop
    reactive:
      url: postgresql://localhost:5432/barber_shop
  hibernate-orm:
    database:
      generation: validate  # o 'update' si quieres auto-update schema
```

---

## 4️⃣ Ejecutar el Proyecto

### Modo Desarrollo (JVM)

```bash
cd c:\Users\ginof\Projects\barber-shop

# Limpiar y compilar
mvn clean compile

# Ejecutar en modo dev
mvn quarkus:dev
```

Output esperado:
```
__  ____  __  _____   ___  __ ____  ______
 --/ __ \/ / / / _ | / _ \/ //_/ / / / __/
-  / /_/ / /_/ / __ |/ __, / ,< / /_/ /\ \
--\___\_\____/_/ |_/_/ |_/_/|_|\____/___/
2024-07-23 17:30:00,000 INFO  [io.quarkus] (main) barber-shop 1.0.0-SNAPSHOT on JVM ...
2024-07-23 17:30:05,000 INFO  [io.quarkus] (main) Listening on: http://localhost:8080
```

### Modo Testing (sin servidor)

```bash
mvn test
```

### Compilar a Executable JAR

```bash
mvn clean package
java -jar target/barber-shop-1.0.0-SNAPSHOT-runner.jar
```

### Compilar a Native Binary (GraalVM)

```bash
mvn clean package -Dnative -Dquarkus.native.container-build=true
./target/barber-shop-1.0.0-SNAPSHOT-runner
```

---

## 5️⃣ Probar Endpoints

Una vez que el servidor esté corriendo en `http://localhost:8080`:

### Items

```bash
# Listar todos los items
curl -s "http://localhost:8080/api/items?page=1&pageSize=10" | jq

# Buscar "fade"
curl -s "http://localhost:8080/api/items?page=1&pageSize=10&search=fade" | jq

# Solo productos
curl -s "http://localhost:8080/api/items?page=1&pageSize=10&category=PRODUCT" | jq

# Crear item
curl -X POST "http://localhost:8080/api/items?page=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Corte Premium",
    "description": "Corte con masaje incluido",
    "category": "SERVICE",
    "price": 35.00,
    "isActive": true
  }' | jq
```

### Clientes

```bash
# Listar clientes
curl -s "http://localhost:8080/api/customers?page=1&pageSize=10" | jq

# Crear cliente
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

### Empleados

```bash
# Listar empleados
curl -s "http://localhost:8080/api/employees?page=1&pageSize=10" | jq

# Crear empleado
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

### Citas

```bash
# Listar citas
curl -s "http://localhost:8080/api/appointments?page=1&pageSize=10" | jq

# Crear cita
curl -X POST "http://localhost:8080/api/appointments?page=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "employeeId": 1,
    "itemId": 1,
    "scheduledAt": "2024-07-24T10:30:00",
    "status": "PENDING"
  }' | jq
```

### Ventas

```bash
# Listar ventas
curl -s "http://localhost:8080/api/sales?page=1&pageSize=10" | jq

# Crear venta completa
curl -X POST "http://localhost:8080/api/sales?page=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "employeeId": 1,
    "paymentMethod": "CASH",
    "discount": 0,
    "items": [
      { "itemId": 1, "quantity": 1, "unitPrice": 25.00 },
      { "itemId": 15, "quantity": 1, "unitPrice": 10.00 }
    ]
  }' | jq
```

### Reportes

```bash
# Obtener reporte del día
curl -s "http://localhost:8080/api/reports/daily/2024-07-23" | jq

# Generar reporte del día
curl -X POST "http://localhost:8080/api/reports/daily/generate/2024-07-23" | jq
```

### Auditoría

```bash
# Listar cambios (audit logs)
curl -s "http://localhost:8080/api/audit-logs?page=1&pageSize=10" | jq

# Filtrar por entidad
curl -s "http://localhost:8080/api/audit-logs?page=1&pageSize=10&entityType=ITEM" | jq

# Filtrar por acción
curl -s "http://localhost:8080/api/audit-logs?page=1&pageSize=10&action=CREATE" | jq
```

---

## 6️⃣ Swagger UI

Acceder a la documentación interactiva:

**http://localhost:8080/q/swagger-ui**

Aquí puedes:
- Ver todos los endpoints
- Leer descripciones de parámetros
- Probar requests directamente
- Ver esquemas de request/response

---

## 7️⃣ Health Check

```bash
# Verificar que el servidor está activo
curl -s http://localhost:8080/q/health | jq

# Respuesta esperada:
# {
#   "status": "UP",
#   "checks": [
#     { "name": "Database connection", "status": "UP" },
#     ...
#   ]
# }
```

---

## 8️⃣ Logging

Ver logs en tiempo real:

```bash
# En modo dev, los logs aparecen en la consola
# Para más detalles, editar src/main/resources/application.yaml:

quarkus:
  log:
    level: INFO
    category:
      "org.barbershop": DEBUG
      "org.hibernate.SQL": DEBUG
```

---

## 9️⃣ Detener el Servidor

En modo dev (Ctrl+C o):

```bash
# El servidor se detiene automáticamente
# Los cambios de código se recargan automáticamente
```

---

## 🔟 Limpiar Base de Datos

Si algo se daña o quieres empezar de cero:

```bash
# Eliminar todas las tablas
psql -U barber_shop -d barber_shop -c "DROP SCHEMA public CASCADE; CREATE SCHEMA public;"

# Recrear schema
psql -U barber_shop -d barber_shop -f database/DDL.sql

# Recargar datos
psql -U barber_shop -d barber_shop -f scripts/seed-items.sql
```

---

## ⚠️ Problemas Comunes

### Error: "Connection refused" a PostgreSQL

```bash
# Verificar que PostgreSQL esté corriendo
pg_isready

# Si no está corriendo:
# - Docker: docker start barbershop-db
# - Local: sudo systemctl start postgresql (Linux) o iniciar desde Services (Windows)
```

### Error: "Port 8080 already in use"

```bash
# Cambiar puerto en application.yaml:
quarkus:
  http:
    port: 8081

# O matar el proceso que usa el puerto
lsof -i :8080  # macOS/Linux
netstat -ano | findstr :8080  # Windows
```

### Error: "BUILD FAILURE" en Maven

```bash
# Limpiar caché y reintentar
mvn clean install -U

# Si sigue, verificar Java version
java -version  # Debe ser 21+
```

---

## 📚 Recursos Adicionales

| Recurso | Ubicación |
|---------|-----------|
| Documentación de arquitectura | `ARCHITECTURE.md` |
| Guía de tests | `TESTS.md` |
| Ejemplos de datos | `scripts/README.md` |
| Especificación OpenAPI | `src/main/resources/openapi.yaml` |
| README del proyecto | `README.md` |

---

## 🎯 Próximos Pasos

1. ✅ Ejecutar `mvn quarkus:dev`
2. ✅ Abrir http://localhost:8080/q/swagger-ui
3. ✅ Probar GET /items
4. ✅ Crear un cliente: POST /customers
5. ✅ Crear una venta: POST /sales
6. ✅ Ver reporte: GET /reports/daily/{date}
7. ✅ Revisar auditoría: GET /audit-logs

---

## 📞 Soporte

Si tienes problemas:

1. Revisar logs en consola
2. Verificar configuración en `application.yaml`
3. Revisar esquema de BD: `psql -U barber_shop -d barber_shop -c "\dt"`
4. Ver documentación en `ARCHITECTURE.md`

¡Que disfrutes desarrollando el barbershop! 🎉
