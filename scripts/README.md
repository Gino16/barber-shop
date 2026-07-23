# 🏪 Datos para Barbería Lima

Archivos para cargar datos iniciales de servicios y productos realistas para una barbería en Lima, Perú.

---

## 📊 Contenido

### Servicios (14 items)

**Para Hombres (9):**
- ✂️ Corte Colegial
- ✂️ Corte Fade
- ✂️ Corte Undercut
- ✂️ Corte Degradado
- ✂️ Corte de Barba
- ✂️ Afeitado Completo
- ✂️ Corte + Barba (Combo)
- ✂️ Tinte/Coloración
- ✂️ Tratamiento Capilar

**Para Mujeres (3):**
- ✂️ Corte Damas
- ✂️ Alisado
- ✂️ Peinado/Estilizado

**Adicionales (2):**
- 👁️ Diseño de Cejas
- 🧖 Hidratación Facial

### Productos (10 items)

- 🧴 Shampoo Premium Hombre
- 🧴 Acondicionador
- 🧴 Pomada Fuerte
- 🧴 Gel Styling
- 🧴 Spray Fijador
- 🧴 Aceite para Barba
- 🧴 Loción Aftershave
- 🧴 Tónico Capilar
- 🧴 Mascarilla Capilar
- 🧴 Crema para Peinar
- 🧴 Colonia Barber

---

## 🚀 Cómo Usar

### Opción 1: SQL (Base de Datos)

Si prefieres cargar los datos directamente en PostgreSQL:

```bash
# Conectar a la BD
psql -U barber_shop -d barber_shop -h localhost

# Importar el archivo SQL
\i /path/to/seed-items.sql

# O desde la terminal
psql -U barber_shop -d barber_shop -h localhost -f seed-items.sql
```

**Ventaja:** Carga todo de una vez

---

### Opción 2: CURL (API REST)

Usar el script bash con curl para crear items uno a uno a través de la API:

```bash
# Hacer el script ejecutable
chmod +x curl-examples.sh

# Ejecutar todos los ejemplos
bash curl-examples.sh

# O ejecutar curl individuales
curl -X POST "http://localhost:8080/api/items?page=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -d '{"name": "Corte Fade", "description": "Fade...", "category": "SERVICE", "active": true}'
```

**Ventaja:** Puedes ver respuestas de cada inserción

---

### Opción 3: Postman

Importar la colección en Postman para pruebas interactivas:

1. Abre Postman
2. Click en **Import**
3. Selecciona `postman-collection.json`
4. Ejecuta los requests desde la UI
5. Visualiza las respuestas

**Ventaja:** Interfaz gráfica, fácil de explorar

---

## 📝 Archivos Incluidos

| Archivo | Descripción |
|---------|------------|
| `seed-items.sql` | Inserciones SQL para PostgreSQL |
| `curl-examples.sh` | Script bash con ejemplos de curl |
| `postman-collection.json` | Colección importable en Postman |
| `README.md` | Este archivo |

---

## 🔍 Ejemplos de Filtrado

Una vez cargados los datos, puedes filtrar:

```bash
# Todos los servicios
curl "http://localhost:8080/api/items?page=1&pageSize=100&category=SERVICE"

# Todos los productos
curl "http://localhost:8080/api/items?page=1&pageSize=100&category=PRODUCT"

# Buscar "fade"
curl "http://localhost:8080/api/items?page=1&pageSize=100&search=fade"

# Buscar "pomada" en productos
curl "http://localhost:8080/api/items?page=1&pageSize=100&search=pomada&category=PRODUCT"

# Ordenar por nombre descendente
curl "http://localhost:8080/api/items?page=1&pageSize=50&sortBy=name&sortDirection=desc"
```

---

## ✅ Validación

Después de cargar, verifica con:

```bash
# Total de items
curl "http://localhost:8080/api/items?page=1&pageSize=100" | jq '.pagination.total'

# Debería ser: 24 items (14 servicios + 10 productos)
```

---

## 💡 Notas

- Todos los items vienen con `active: true` por defecto
- Puedes desactivar items actualizándolos: `PUT /items/{id}` con `"active": false`
- Los servicios son para hombres principalmente, con opciones limitadas para mujeres
- Los productos están enfocados en cuidado capilar y barba
- Puedes añadir más items según las necesidades del negocio

---

## 🔧 Personalización

Para modificar los datos, edita:

- `seed-items.sql` - Modifica descripciones, categorías, etc.
- `curl-examples.sh` - Ajusta nombres o descripciones
- `postman-collection.json` - Actualiza los payloads

Luego, vuelve a cargar los datos (primero elimina los anteriores si es necesario).

---

## 🆘 Troubleshooting

### "Connection refused" en curl
- Verifica que la API está corriendo: `http://localhost:8080/api`

### Error SQL "UNIQUE constraint"
- Es posible que los datos ya estén cargados
- Limpia la tabla: `DELETE FROM items;` y vuelve a intentar

### Postman no importa el JSON
- Verifica que el archivo sea válido: `jq . postman-collection.json`

---

## 📞 Soporte

Para preguntas sobre items específicos o modificaciones:
- Revisa el OpenAPI: `http://localhost:8080/q/swagger-ui`
- Consulta el README principal del proyecto
