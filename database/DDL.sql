-- Catálogo de servicios o productos
CREATE TABLE items
(
    id             SERIAL PRIMARY KEY,
    nombre         VARCHAR(100)   NOT NULL,
    descripcion    TEXT,
    categoria      VARCHAR(50), -- 'SERVICIO', 'PRODUCTO'
    activo         BOOLEAN   DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Cabecera de la venta
CREATE TABLE ventas
(
    id          SERIAL PRIMARY KEY,
    fecha_venta TIMESTAMP               DEFAULT CURRENT_TIMESTAMP,
    total       DECIMAL(10, 2) NOT NULL DEFAULT 0,
    metodo_pago VARCHAR(20), -- 'EFECTIVO', 'TRANSFERENCIA', 'TARJETA'
    notas       TEXT
);

-- Detalle de la venta (Extensibilidad: una venta puede tener N servicios/productos)
CREATE TABLE detalles_venta
(
    id              SERIAL PRIMARY KEY,
    venta_id        INTEGER REFERENCES ventas (id) ON DELETE CASCADE,
    item_id         INTEGER REFERENCES items (id),
    cantidad        INTEGER        NOT NULL DEFAULT 1,
    precio_unitario DECIMAL(10, 2) NOT NULL,
    subtotal        DECIMAL(10, 2) NOT NULL
);