-- ============================================
-- Datos iniciales para Barbería Lima
-- Servicios y Productos
-- ============================================

-- ==================== SERVICIOS PARA HOMBRES ====================
-- Corte Colegial
INSERT INTO items (name, description, category, is_active, created_at)
VALUES ('Corte Colegial', 'Corte tradicional de colegial, largo uniforme con capas ligeras', 'SERVICE', true, CURRENT_TIMESTAMP);

-- Corte Fade
INSERT INTO items (name, description, category, is_active, created_at)
VALUES ('Corte Fade', 'Fade classic o moderno, degradado gradual de los lados al tope', 'SERVICE', true, CURRENT_TIMESTAMP);

-- Corte Undercut
INSERT INTO items (name, description, category, is_active, created_at)
VALUES ('Corte Undercut', 'Lados muy cortos, tope largo y definido, estilo moderno', 'SERVICE', true, CURRENT_TIMESTAMP);

-- Corte Degradado
INSERT INTO items (name, description, category, is_active, created_at)
VALUES ('Corte Degradado', 'Degradado suave de los lados manteniendo volumen en la corona', 'SERVICE', true, CURRENT_TIMESTAMP);

-- Corte de Barba
INSERT INTO items (name, description, category, is_active, created_at)
VALUES ('Corte de Barba', 'Perfilado y definición de barba, incluye limpieza de mejillas y cuello', 'SERVICE', true, CURRENT_TIMESTAMP);

-- Afeitado Completo
INSERT INTO items (name, description, category, is_active, created_at)
VALUES ('Afeitado Completo', 'Afeitado a navaja con espuma caliente y toalla húmeda', 'SERVICE', true, CURRENT_TIMESTAMP);

-- Corte + Barba (Combo)
INSERT INTO items (name, description, category, is_active, created_at)
VALUES ('Corte + Barba (Combo)', 'Corte de cabello + perfilado de barba, precio especial', 'SERVICE', true, CURRENT_TIMESTAMP);

-- Tinte/Coloración
INSERT INTO items (name, description, category, is_active, created_at)
VALUES ('Tinte/Coloración', 'Coloración de cabello con productos premium, aplicación profesional', 'SERVICE', true, CURRENT_TIMESTAMP);

-- Tratamiento Capilar
INSERT INTO items (name, description, category, is_active, created_at)
VALUES ('Tratamiento Capilar', 'Hidratación y regeneración profunda, incluye masaje capilar', 'SERVICE', true, CURRENT_TIMESTAMP);

-- ==================== SERVICIOS PARA MUJERES ====================
-- Corte Damas
INSERT INTO items (name, description, category, is_active, created_at)
VALUES ('Corte Damas', 'Corte personalizado para mujeres con o sin capas, según preferencia', 'SERVICE', true, CURRENT_TIMESTAMP);

-- Alisado
INSERT INTO items (name, description, category, is_active, created_at)
VALUES ('Alisado', 'Alisado permanente o temporal con tratamientos de alta calidad', 'SERVICE', true, CURRENT_TIMESTAMP);

-- Peinado/Estilizado
INSERT INTO items (name, description, category, is_active, created_at)
VALUES ('Peinado/Estilizado', 'Peinado profesional para eventos especiales', 'SERVICE', true, CURRENT_TIMESTAMP);

-- ==================== SERVICIOS ADICIONALES ====================
-- Diseño de Cejas
INSERT INTO items (name, description, category, is_active, created_at)
VALUES ('Diseño de Cejas', 'Perfilado y diseño de cejas para hombres y mujeres', 'SERVICE', true, CURRENT_TIMESTAMP);

-- Hidratación Facial
INSERT INTO items (name, description, category, is_active, created_at)
VALUES ('Hidratación Facial', 'Limpieza y hidratación facial con productos premium', 'SERVICE', true, CURRENT_TIMESTAMP);

-- ==================== PRODUCTOS PARA CABELLO ====================
-- Shampoo Premium Hombre
INSERT INTO items (name, description, category, is_active, created_at)
VALUES ('Shampoo Premium Hombre', 'Shampoo profesional para hombres, fórmula 2 en 1 champú + acondicionador', 'PRODUCT', true, CURRENT_TIMESTAMP);

-- Acondicionador
INSERT INTO items (name, description, category, is_active, created_at)
VALUES ('Acondicionador', 'Acondicionador nutritivo para hidratación profunda', 'PRODUCT', true, CURRENT_TIMESTAMP);

-- Pomada Fuerte
INSERT INTO items (name, description, category, is_active, created_at)
VALUES ('Pomada Fuerte', 'Pomada de fijación fuerte, acabado brillante, estilo clásico', 'PRODUCT', true, CURRENT_TIMESTAMP);

-- Gel Styling
INSERT INTO items (name, description, category, is_active, created_at)
VALUES ('Gel Styling', 'Gel de fijación media, acabado natural sin brillo', 'PRODUCT', true, CURRENT_TIMESTAMP);

-- Spray Fijador
INSERT INTO items (name, description, category, is_active, created_at)
VALUES ('Spray Fijador', 'Spray para fijar peinado durante todo el día', 'PRODUCT', true, CURRENT_TIMESTAMP);

-- Aceite para Barba
INSERT INTO items (name, description, category, is_active, created_at)
VALUES ('Aceite para Barba', 'Aceite hidratante y suavizante para barba, con aroma a madera', 'PRODUCT', true, CURRENT_TIMESTAMP);

-- Loción Aftershave
INSERT INTO items (name, description, category, is_active, created_at)
VALUES ('Loción Aftershave', 'Loción refrescante post-afeitado, desinfectante y aromática', 'PRODUCT', true, CURRENT_TIMESTAMP);

-- Tónico Capilar
INSERT INTO items (name, description, category, is_active, created_at)
VALUES ('Tónico Capilar', 'Tónico estimulante para crecimiento y fortaleza del cabello', 'PRODUCT', true, CURRENT_TIMESTAMP);

-- Mascarilla Capilar
INSERT INTO items (name, description, category, is_active, created_at)
VALUES ('Mascarilla Capilar', 'Mascarilla de tratamiento profundo para cabello seco o dañado', 'PRODUCT', true, CURRENT_TIMESTAMP);

-- Crema para Peinar
INSERT INTO items (name, description, category, is_active, created_at)
VALUES ('Crema para Peinar', 'Crema ligera para peinar sin necesidad de agua', 'PRODUCT', true, CURRENT_TIMESTAMP);

-- Colonia Barber
INSERT INTO items (name, description, category, is_active, created_at)
VALUES ('Colonia Barber', 'Colonia clásica con aroma barbero, 100ml', 'PRODUCT', true, CURRENT_TIMESTAMP);

-- Total: 24 items (14 servicios + 10 productos)
