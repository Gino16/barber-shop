# ✨ Barber Shop - API REST

Sistema de gestión de servicios, productos y ventas para una barbería. Construido con **Quarkus** y **PostgreSQL** siguiendo arquitectura hexagonal.

## 📋 Tabla de Contenidos

- [Requisitos](#requisitos)
- [Tecnologías](#tecnologías)
- [Setup](#setup)
- [Ejecutar el Proyecto](#ejecutar-el-proyecto)
- [API Endpoints](#api-endpoints)
- [Ejemplos de Uso](#ejemplos-de-uso)
- [Estructura del Proyecto](#estructura-del-proyecto)

---

## 🔧 Requisitos

- **Java 21** o superior
- **Maven 3.8+**
- **Docker & Docker Compose** (para PostgreSQL)
- **curl** o **Postman** (para probar endpoints)

---

## 🛠️ Tecnologías

| Tecnología | Versión | Propósito |
|-----------|---------|----------|
| **Quarkus** | 3.37.3 | Framework REST ligero y rápido |
| **Java** | 21 | Lenguaje principal |
| **PostgreSQL** | Latest | Base de datos relacional |
| **Hibernate Panache** | - | ORM simplificado |
| **JAX-RS** | - | Especificación REST |
| **OpenAPI/Swagger** | 7.10.0 | Documentación automática de API |
| **JUnit** | - | Testing unitario |
| **Rest-Assured** | - | Testing de API REST |

---

## 📦 Setup

### 1. Clonar el repositorio

```bash
git clone <repository-url>
cd barber-shop
```

### 2. Configurar PostgreSQL

Copiar el archivo de configuración de ejemplo:

```bash
cp devops/.env.example devops/.env
```

Ajustar credenciales si es necesario (por defecto están configuradas):

```env
POSTGRES_DB=barber_shop
POSTGRES_USER=barber_shop
POSTGRES_PASSWORD=barber_shop
POSTGRES_PORT=5432
```

### 3. Iniciar la Base de Datos

```bash
docker compose --env-file devops/.env -f devops/docker-compose.yml up -d
```

La esquema en `database/DDL.sql` se aplica automáticamente en el primer inicio del volumen de la base de datos.

Verificar que el contenedor está corriendo:

```bash
docker ps | grep postgres
```

---

## 🚀 Ejecutar el Proyecto

### Modo Desarrollo

```bash
./mvnw clean quarkus:dev
```

La aplicación estará disponible en: **http://localhost:8080**

Documentación OpenAPI: **http://localhost:8080/q/openapi**

Swagger UI: **http://localhost:8080/q/swagger-ui**

### Modo Producción (JAR)

```bash
./mvnw clean package
java -jar target/barber-shop-1.0.0-SNAPSHOT-runner.jar
```

### Modo Nativo (GraalVM)

```bash
./mvnw clean package -Pnative
./target/barber-shop-1.0.0-SNAPSHOT-runner
```

### Tests

```bash
./mvnw clean test
```

---

## 📡 API Endpoints

### Items (Servicios y Productos)

#### Listar todos los items

```http
GET /api/items
```

**Respuesta:**

```json
[
  {
    "id": 1,
    "name": "Corte de cabello",
    "description": "Corte moderno con navaja",
    "category": "SERVICE",
    "active": true,
    "createdAt": "2026-07-23T16:30:00Z"
  }
]
```

#### Obtener un item específico

```http
GET /api/items/{id}
```

**Ejemplo:**

```http
GET /api/items/1
```

**Respuesta:**

```json
{
  "id": 1,
  "name": "Corte de cabello",
  "description": "Corte moderno con navaja",
  "category": "SERVICE",
  "active": true,
  "createdAt": "2026-07-23T16:30:00Z"
}
```

#### Crear un nuevo item

```http
POST /api/items
Content-Type: application/json

{
  "name": "Afeitado completo",
  "description": "Afeitado con esmero",
  "category": "SERVICE",
  "active": true
}
```

**Respuesta (201 Created):**

```json
{
  "id": 2,
  "name": "Afeitado completo",
  "description": "Afeitado con esmero",
  "category": "SERVICE",
  "active": true,
  "createdAt": "2026-07-23T16:31:00Z"
}
```

#### Actualizar un item

```http
PUT /api/items/{id}
Content-Type: application/json

{
  "name": "Corte premium",
  "description": "Corte con diseño personalizado",
  "category": "SERVICE",
  "active": true
}
```

**Respuesta:**

```json
{
  "id": 1,
  "name": "Corte premium",
  "description": "Corte con diseño personalizado",
  "category": "SERVICE",
  "active": true,
  "createdAt": "2026-07-23T16:30:00Z"
}
```

---

## 💡 Ejemplos de Uso

### Con cURL

#### Listar todos los items

```bash
curl -X GET http://localhost:8080/api/items
```

#### Crear un nuevo item

```bash
curl -X POST http://localhost:8080/api/items \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Tratamiento capilar",
    "description": "Tratamiento hidratante con keratina",
    "category": "SERVICE",
    "active": true
  }'
```

#### Obtener un item

```bash
curl -X GET http://localhost:8080/api/items/1
```

#### Actualizar un item

```bash
curl -X PUT http://localhost:8080/api/items/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Corte clásico",
    "description": "Corte tradicional",
    "category": "SERVICE",
    "active": true
  }'
```

### Con Postman

1. Importar la colección desde: `src/main/resources/openapi.yaml`
2. Establecer variable de entorno: `base_url = http://localhost:8080`
3. Probar los endpoints desde Postman

---

## 📂 Estructura del Proyecto

```
barber-shop/
├── src/
│   ├── main/
│   │   ├── java/org/barbershop/
│   │   │   └── item/
│   │   │       ├── domain/           # Entidades de negocio
│   │   │       ├── application/      # Servicios y casos de uso
│   │   │       └── adapter/
│   │   │           ├── in/rest/      # Controladores REST
│   │   │           └── out/persistence/  # Repositorios JPA
│   │   └── resources/
│   │       ├── application.yml       # Configuración Quarkus
│   │       ├── openapi.yaml          # Especificación OpenAPI
│   │       └── import.sql            # Datos iniciales
│   └── test/                         # Tests unitarios e integración
├── database/
│   └── DDL.sql                       # Esquema de base de datos
├── devops/
│   ├── docker-compose.yml            # Stack de contenedores
│   └── .env.example                  # Variables de entorno
├── pom.xml                           # Configuración Maven
└── README.md                         # Este archivo
```

---

## 🗄️ Base de Datos

### Tablas

#### `items`

Catálogo de servicios y productos.

```sql
CREATE TABLE items (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    category VARCHAR(50),              -- 'SERVICE', 'PRODUCT'
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### `sales`

Encabezado de ventas.

```sql
CREATE TABLE sales (
    id SERIAL PRIMARY KEY,
    sold_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10, 2) DEFAULT 0,
    payment_method VARCHAR(20),        -- 'CASH', 'TRANSFER', 'CARD'
    notes TEXT
);
```

#### `sale_items`

Detalles de ventas (relación muchos-a-muchos).

```sql
CREATE TABLE sale_items (
    id SERIAL PRIMARY KEY,
    sale_id INTEGER REFERENCES sales(id) ON DELETE CASCADE,
    item_id INTEGER REFERENCES items(id),
    quantity INTEGER DEFAULT 1,
    unit_price DECIMAL(10, 2) NOT NULL,
    subtotal_amount DECIMAL(10, 2) NOT NULL
);
```

---

## 🔍 Debugging

### Ver logs en tiempo real

```bash
./mvnw quarkus:dev
```

### Acceder a la consola de Swagger

```
http://localhost:8080/q/swagger-ui.html
```

---

## 📝 Notas

- La arquitectura sigue **Hexagonal Architecture (Ports & Adapters)**
- Los datos iniciales se cargan desde `src/main/resources/import.sql`
- La aplicación se reconstruye automáticamente en modo `dev` al detectar cambios

---

## 🤝 Contribuir

Para contribuir al proyecto:

1. Crear una rama: `git checkout -b feature/mi-feature`
2. Hacer commits descriptivos
3. Push a la rama: `git push origin feature/mi-feature`
4. Crear un Pull Request

---

## 📄 Licencia

Este proyecto es de uso privado.
