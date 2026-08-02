# Datos de demostración

Estos archivos cargan el catálogo de ejemplo (servicios y productos):

| Archivo | Uso |
| --- | --- |
| `seed-items.sql` | Inserción directa en PostgreSQL |
| `curl-examples.sh` | Ejemplos de creación mediante HTTP |
| `postman-collection.json` | Requests para importar en Postman |
| `load-items.sh` | Atajo para cargar el catálogo |

## SQL

Con PostgreSQL local:

```bash
psql -h localhost -U barber_shop -d barber_shop -f scripts/seed-items.sql
```

Con el contenedor del proyecto:

```powershell
Get-Content scripts\seed-items.sql | docker exec -i barber-shop-postgres psql -U barber_shop -d barber_shop
```

## API

Inicia la aplicación y ejecuta desde Git Bash o WSL:

```bash
bash scripts/curl-examples.sh
```

La colección usa `http://localhost:8080` como base. Para verificar la carga:

```bash
curl "http://localhost:8080/api/items?page=1&pageSize=100"
```

El catálogo incluido contiene 24 registros según el script actual. Si se ejecuta más de
una vez, pueden producirse duplicados; revisa `seed-items.sql` antes de recargar una base
que ya tenga datos.
