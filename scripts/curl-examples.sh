# Ejemplos de CURL para agregar items a la barbería
# Asegúrate de que la API esté ejecutándose: http://localhost:8080/api

BASE_URL="http://localhost:8080/api"

# ==================== SERVICIOS PARA HOMBRES ====================

# 1. Corte Colegial
curl -X POST "$BASE_URL/items?page=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Corte Colegial",
    "description": "Corte tradicional de colegial, largo uniforme con capas ligeras",
    "category": "SERVICE",
    "active": true
  }'

# 2. Corte Fade
curl -X POST "$BASE_URL/items?page=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Corte Fade",
    "description": "Fade classic o moderno, degradado gradual de los lados al tope",
    "category": "SERVICE",
    "active": true
  }'

# 3. Corte Undercut
curl -X POST "$BASE_URL/items?page=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Corte Undercut",
    "description": "Lados muy cortos, tope largo y definido, estilo moderno",
    "category": "SERVICE",
    "active": true
  }'

# 4. Corte Degradado
curl -X POST "$BASE_URL/items?page=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Corte Degradado",
    "description": "Degradado suave de los lados manteniendo volumen en la corona",
    "category": "SERVICE",
    "active": true
  }'

# 5. Corte de Barba
curl -X POST "$BASE_URL/items?page=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Corte de Barba",
    "description": "Perfilado y definición de barba, incluye limpieza de mejillas y cuello",
    "category": "SERVICE",
    "active": true
  }'

# 6. Afeitado Completo
curl -X POST "$BASE_URL/items?page=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Afeitado Completo",
    "description": "Afeitado a navaja con espuma caliente y toalla húmeda",
    "category": "SERVICE",
    "active": true
  }'

# 7. Corte + Barba (Combo)
curl -X POST "$BASE_URL/items?page=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Corte + Barba (Combo)",
    "description": "Corte de cabello + perfilado de barba, precio especial",
    "category": "SERVICE",
    "active": true
  }'

# 8. Tinte/Coloración
curl -X POST "$BASE_URL/items?page=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Tinte/Coloración",
    "description": "Coloración de cabello con productos premium, aplicación profesional",
    "category": "SERVICE",
    "active": true
  }'

# 9. Tratamiento Capilar
curl -X POST "$BASE_URL/items?page=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Tratamiento Capilar",
    "description": "Hidratación y regeneración profunda, incluye masaje capilar",
    "category": "SERVICE",
    "active": true
  }'

# ==================== SERVICIOS PARA MUJERES ====================

# 10. Corte Damas
curl -X POST "$BASE_URL/items?page=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Corte Damas",
    "description": "Corte personalizado para mujeres con o sin capas, según preferencia",
    "category": "SERVICE",
    "active": true
  }'

# 11. Alisado
curl -X POST "$BASE_URL/items?page=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Alisado",
    "description": "Alisado permanente o temporal con tratamientos de alta calidad",
    "category": "SERVICE",
    "active": true
  }'

# 12. Peinado/Estilizado
curl -X POST "$BASE_URL/items?page=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Peinado/Estilizado",
    "description": "Peinado profesional para eventos especiales",
    "category": "SERVICE",
    "active": true
  }'

# ==================== SERVICIOS ADICIONALES ====================

# 13. Diseño de Cejas
curl -X POST "$BASE_URL/items?page=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Diseño de Cejas",
    "description": "Perfilado y diseño de cejas para hombres y mujeres",
    "category": "SERVICE",
    "active": true
  }'

# 14. Hidratación Facial
curl -X POST "$BASE_URL/items?page=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Hidratación Facial",
    "description": "Limpieza y hidratación facial con productos premium",
    "category": "SERVICE",
    "active": true
  }'

# ==================== PRODUCTOS PARA CABELLO ====================

# 15. Shampoo Premium Hombre
curl -X POST "$BASE_URL/items?page=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Shampoo Premium Hombre",
    "description": "Shampoo profesional para hombres, fórmula 2 en 1 champú + acondicionador",
    "category": "PRODUCT",
    "active": true
  }'

# 16. Acondicionador
curl -X POST "$BASE_URL/items?page=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Acondicionador",
    "description": "Acondicionador nutritivo para hidratación profunda",
    "category": "PRODUCT",
    "active": true
  }'

# 17. Pomada Fuerte
curl -X POST "$BASE_URL/items?page=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Pomada Fuerte",
    "description": "Pomada de fijación fuerte, acabado brillante, estilo clásico",
    "category": "PRODUCT",
    "active": true
  }'

# 18. Gel Styling
curl -X POST "$BASE_URL/items?page=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Gel Styling",
    "description": "Gel de fijación media, acabado natural sin brillo",
    "category": "PRODUCT",
    "active": true
  }'

# 19. Spray Fijador
curl -X POST "$BASE_URL/items?page=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Spray Fijador",
    "description": "Spray para fijar peinado durante todo el día",
    "category": "PRODUCT",
    "active": true
  }'

# 20. Aceite para Barba
curl -X POST "$BASE_URL/items?page=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Aceite para Barba",
    "description": "Aceite hidratante y suavizante para barba, con aroma a madera",
    "category": "PRODUCT",
    "active": true
  }'

# 21. Loción Aftershave
curl -X POST "$BASE_URL/items?page=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Loción Aftershave",
    "description": "Loción refrescante post-afeitado, desinfectante y aromática",
    "category": "PRODUCT",
    "active": true
  }'

# 22. Tónico Capilar
curl -X POST "$BASE_URL/items?page=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Tónico Capilar",
    "description": "Tónico estimulante para crecimiento y fortaleza del cabello",
    "category": "PRODUCT",
    "active": true
  }'

# 23. Mascarilla Capilar
curl -X POST "$BASE_URL/items?page=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Mascarilla Capilar",
    "description": "Mascarilla de tratamiento profundo para cabello seco o dañado",
    "category": "PRODUCT",
    "active": true
  }'

# 24. Crema para Peinar
curl -X POST "$BASE_URL/items?page=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Crema para Peinar",
    "description": "Crema ligera para peinar sin necesidad de agua",
    "category": "PRODUCT",
    "active": true
  }'

# 25. Colonia Barber
curl -X POST "$BASE_URL/items?page=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Colonia Barber",
    "description": "Colonia clásica con aroma barbero, 100ml",
    "category": "PRODUCT",
    "active": true
  }'

# ==================== EJEMPLOS DE FILTRADO ====================

# Listar todos los servicios
# curl "http://localhost:8080/api/items?page=1&pageSize=100&category=SERVICE"

# Listar todos los productos
# curl "http://localhost:8080/api/items?page=1&pageSize=100&category=PRODUCT"

# Buscar servicios con "fade"
# curl "http://localhost:8080/api/items?page=1&pageSize=100&search=fade&category=SERVICE"

# Buscar productos con "pomada"
# curl "http://localhost:8080/api/items?page=1&pageSize=100&search=pomada&category=PRODUCT"
