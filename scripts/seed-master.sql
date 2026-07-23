-- ============================================
-- Seed Data: Employees, Customers, Inventory
-- Barbería Lima - Datos iniciales
-- ============================================

-- ==================== EMPLOYEES ====================
-- Equipo de la barbería

INSERT INTO employees (name, email, phone, role, salary, is_active, hired_date) VALUES
('Carlos Mendoza',    'carlos@barberia.pe',    '987-111-001', 'MANAGER', 3500.00, TRUE, '2022-01-10'),
('Diego Quispe',      'diego@barberia.pe',     '987-111-002', 'BARBER',  2000.00, TRUE, '2022-03-15'),
('Luis Huamán',       'luis@barberia.pe',      '987-111-003', 'BARBER',  2000.00, TRUE, '2022-06-01'),
('Andrés Ccopa',      'andres@barberia.pe',    '987-111-004', 'BARBER',  1800.00, TRUE, '2023-01-20'),
('Sofía Vargas',      'sofia@barberia.pe',     '987-111-005', 'STYLIST', 1900.00, TRUE, '2023-04-10'),
('Marco Palacios',    'marco@barberia.pe',     '987-111-006', 'BARBER',  1800.00, FALSE,'2021-08-01');

-- ==================== CUSTOMERS ====================
-- Base de clientes registrados

INSERT INTO customers (name, email, phone, address, notes, is_active) VALUES
-- Clientes frecuentes
('Juan Carlos Pérez',   'jcperez@gmail.com',      '981-001-001', 'Av. Arequipa 1250, Miraflores',   'Cliente VIP, prefiere fade bajo',        TRUE),
('Miguel Ángel Torres', 'matorres@hotmail.com',   '981-001-002', 'Jr. Lampa 340, Lima Centro',      'Viene cada 2 semanas, corte colegial',   TRUE),
('Roberto Silva',       'rsilva@outlook.com',     '981-001-003', 'Calle Los Álamos 88, San Borja',  'Alérgico a algunos productos de barba',  TRUE),
('Erick Castillo',      NULL,                     '981-001-004', 'Av. Brasil 520, Pueblo Libre',    'Prefiere atención con Diego',            TRUE),
('Fernando Mamani',     'fmamani@gmail.com',      '981-001-005', 'Av. Universitaria 1800, SMP',     NULL,                                     TRUE),
('José Luis Chávez',    'jlchavez@gmail.com',     '981-001-006', 'Jr. Carabaya 650, Lima',          'Trae a su hijo, corte colegial doble',   TRUE),
('Paolo Gutiérrez',     'paolo.g@gmail.com',      '981-001-007', 'Calle Berlín 280, Miraflores',    'Pide siempre pomada Reuzel',             TRUE),
('Rodrigo Valenzuela',  NULL,                     '981-001-008', 'Av. La Marina 3200, San Miguel',  NULL,                                     TRUE),
('Alejandro Núñez',     'alexnunez@gmail.com',    '981-001-009', 'Av. Javier Prado 1400, San Isidro','Cliente corporativo, ejecutivo',        TRUE),
('César Huanca',        'chuanca@yahoo.com',      '981-001-010', 'Jr. Ancash 1190, Lima',           NULL,                                     TRUE),
-- Clientes ocasionales
('Bruno Ramos',         NULL,                     '981-001-011', NULL,                              'Primera vez',                            TRUE),
('Sebastián Flores',    'sebflores@gmail.com',    '981-001-012', 'Av. Angamos 800, Surquillo',      NULL,                                     TRUE),
('Gianfranco Lozano',   'glozano@gmail.com',      '981-001-013', 'Calle Schell 390, Miraflores',    'Fade + barba siempre',                   TRUE),
('Alonso Reyes',        NULL,                     '981-001-014', NULL,                              NULL,                                     TRUE),
('Daniel Espinoza',     'despinoza@gmail.com',    '981-001-015', 'Jr. Quilca 195, Lima Centro',     NULL,                                     TRUE),
-- Clientas
('Valeria Quispe',      'vquispe@gmail.com',      '981-001-016', 'Av. Conquistadores 1010, SJM',    'Corte y alisado, no corte muy corto',    TRUE),
('Camila Torres',       'ctorres@hotmail.com',    '981-001-017', 'Av. Benavides 3400, Miraflores',  'Solo Sofía la atiende',                  TRUE),
('Lucía Mendoza',       'lmendoza@gmail.com',     '981-001-018', 'Calle Las Begonias 640, SIS',     'Peinado para eventos',                   TRUE),
-- Inactivo
('Héctor Rojas',        'hrojas@gmail.com',       '981-001-019', 'Jr. Huancavelica 298, Lima',      'No renovó membresía',                    FALSE);

-- ==================== APPOINTMENTS ====================
-- Citas de los próximos días

-- Citas de hoy
INSERT INTO appointments (customer_id, employee_id, item_id, scheduled_at, status, notes) VALUES
(1,  2, 2,  NOW() + INTERVAL '1 hour',          'PENDING',   'Fade bajo, degradado lateral'),
(3,  3, 5,  NOW() + INTERVAL '2 hours',         'PENDING',   'Corte + barba, combo completo'),
(6,  4, 1,  NOW() + INTERVAL '3 hours',         'PENDING',   'Corte colegial para el hijo también'),
(9,  2, 1,  NOW() + INTERVAL '4 hours',         'PENDING',   'Ejecutivo, puntual por favor'),
(13, 3, 7,  NOW() + INTERVAL '5 hours',         'PENDING',   'Fade + barba'),
-- Citas de mañana
(2,  2, 1,  NOW() + INTERVAL '1 day 1 hour',    'PENDING',   NULL),
(4,  3, 2,  NOW() + INTERVAL '1 day 2 hours',   'PENDING',   'Fade classic'),
(7,  4, 2,  NOW() + INTERVAL '1 day 3 hours',   'PENDING',   NULL),
(10, 2, 5,  NOW() + INTERVAL '1 day 4 hours',   'PENDING',   'Barba larga, solo perfilar'),
(16, 5, 10, NOW() + INTERVAL '1 day 5 hours',   'PENDING',   'Corte damas, no muy corto'),
-- Citas pasadas (completadas)
(1,  2, 2,  NOW() - INTERVAL '7 days',          'COMPLETED', NULL),
(2,  3, 1,  NOW() - INTERVAL '7 days',          'COMPLETED', NULL),
(5,  4, 7,  NOW() - INTERVAL '7 days',          'COMPLETED', NULL),
(8,  2, 2,  NOW() - INTERVAL '14 days',         'COMPLETED', NULL),
(3,  3, 5,  NOW() - INTERVAL '14 days',         'COMPLETED', NULL),
-- Una cancelada
(11, 2, 1,  NOW() - INTERVAL '3 days',          'CANCELLED', 'No se presentó');

-- ==================== INVENTORY ====================
-- Stock inicial de productos (solo items de categoría PRODUCT)

INSERT INTO inventory (item_id, quantity, min_quantity, cost_price)
SELECT i.id,
       CASE i.name
           WHEN 'Shampoo Premium Hombre'    THEN 15
           WHEN 'Acondicionador'            THEN 12
           WHEN 'Pomada Fuerte'             THEN 20
           WHEN 'Gel Styling'              THEN 18
           WHEN 'Spray Fijador'            THEN 10
           WHEN 'Aceite para Barba'        THEN 14
           WHEN 'Loción Aftershave'        THEN 16
           WHEN 'Tónico Capilar'           THEN 8
           WHEN 'Mascarilla Capilar'        THEN 6
           WHEN 'Crema para Peinar'        THEN 12
           WHEN 'Colonia Barber'           THEN 9
           ELSE 10
       END AS quantity,
       5 AS min_quantity,
       ROUND((i.price * 0.55)::numeric, 2) AS cost_price  -- costo ~55% del precio de venta
FROM items i
WHERE i.category = 'PRODUCT';
