#!/bin/bash

# Script para cargar items iniciales en la barbería
# API Base URL
API_URL="http://localhost:8080/api"

echo "🏪 Cargando servicios y productos de barbería..."
echo ""

# ==================== SERVICIOS PARA HOMBRES ====================
echo "📌 Creando servicios para hombres..."

# Corte Colegial
curl -X POST "$API_URL/items?page=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Corte Colegial",
    "description": "Corte tradicional de colegial, largo uniforme con capas ligeras",
    "category": "SERVICE",
    "active": true
  }' && echo " ✓ Corte Colegial" || echo " ✗ Error"

# Corte Fade
curl -X POST "$API_URL/items?page=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Corte Fade",
    "description": "Fade classic o moderno, degradado gradual de los lados al tope",
    "category": "SERVICE",
    "active": true
  }' && echo " ✓ Corte Fade" || echo " ✗ Error"

# Corte Undercut
curl -X POST "$API_URL/items?page=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Corte Undercut",
    "description": "Lados muy cortos, tope largo y definido, estilo moderno",
    "category": "SERVICE",
    "active": true
  }' && echo " ✓ Corte Undercut" || echo " ✗ Error"

# Corte Degradado
curl -X POST "$API_URL/items?page=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Corte Degradado",
    "description": "Degradado suave de los lados manteniendo volumen en la corona",
    "category": "SERVICE",
    "active": true
  }' && echo " ✓ Corte Degradado" || echo " ✗ Error"

# Corte de Barba
curl -X POST "$API_URL/items?page=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Corte de Barba",
    "description": "Perfilado y definición de barba, incluye limpieza de mejillas y cuello",
    "category": "SERVICE",
    "active": true
  }' && echo " ✓ Corte de Barba" || echo " ✗ Error"

# Afeitado Completo
curl -X POST "$API_URL/items?page=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Afeitado Completo",
    "description": "Afeitado a navaja con espuma caliente y toalla húmeda",
    "category": "SERVICE",
    "active": true
  }' && echo " ✓ Afeitado Completo" || echo " ✗ Error"

# Corte + Barba (Combo)
curl -X POST "$API_URL/items?page=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Corte + Barba (Combo)",
    "description": "Corte de cabello + perfilado de barba, precio especial",
    "category": "SERVICE",
    "active": true
  }' && echo " ✓ Corte + Barba" || echo " ✗ Error"

# Tinte/Coloración
curl -X POST "$API_URL/items?page=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Tinte/Coloración",
    "description": "Coloración de cabello con productos premium, aplicación profesional",
    "category": "SERVICE",
    "active": true
  }' && echo " ✓ Tinte/Coloración" || echo " ✗ Error"

# Tratamiento Capilar
curl -X POST "$API_URL/items?page=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Tratamiento Capilar",
    "description": "Hidratación y regeneración profunda, incluye masaje capilar",
    "category": "SERVICE",
    "active": true
  }' && echo " ✓ Tratamiento Capilar" || echo " ✗ Error"

# ==================== SERVICIOS PARA MUJERES ====================
echo ""
echo "📌 Creando servicios para mujeres..."

# Corte Damas
curl -X POST "$API_URL/items?page=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Corte Damas",
    "description": "Corte personalizado para mujeres con o sin capas, según preferencia",
    "category": "SERVICE",
    "active": true
  }' && echo " ✓ Corte Damas" || echo " ✗ Error"

# Alisado
curl -X POST "$API_URL/items?page=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Alisado",
    "description": "Alisado permanente o temporal con tratamientos de alta calidad",
    "category": "SERVICE",
    "active": true
  }' && echo " ✓ Alisado" || echo " ✗ Error"

# Peinado/Estilizado
curl -X POST "$API_URL/items?page=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Peinado/Estilizado",
    "description": "Peinado profesional para eventos especiales",
    "category": "SERVICE",
    "active": true
  }' && echo " ✓ Peinado/Estilizado" || echo " ✗ Error"

# ==================== SERVICIOS ADICIONALES ====================
echo ""
echo "📌 Creando servicios adicionales..."

# Cejas
curl -X POST "$API_URL/items?page=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Diseño de Cejas",
    "description": "Perfilado y diseño de cejas para hombres y mujeres",
    "category": "SERVICE",
    "active": true
  }' && echo " ✓ Diseño de Cejas" || echo " ✗ Error"

# Hidratación Facial
curl -X POST "$API_URL/items?page=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Hidratación Facial",
    "description": "Limpieza y hidratación facial con productos premium",
    "category": "SERVICE",
    "active": true
  }' && echo " ✓ Hidratación Facial" || echo " ✗ Error"

# ==================== PRODUCTOS PARA CABELLO ====================
echo ""
echo "🛍️  Creando productos para cuidado de cabello..."

# Shampoo Premium
curl -X POST "$API_URL/items?page=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Shampoo Premium Hombre",
    "description": "Shampoo profesional para hombres, fórmula 2 en 1 champu + acondicionador",
    "category": "PRODUCT",
    "active": true
  }' && echo " ✓ Shampoo Premium" || echo " ✗ Error"

# Acondicionador
curl -X POST "$API_URL/items?page=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Acondicionador",
    "description": "Acondicionador nutritivo para hidratación profunda",
    "category": "PRODUCT",
    "active": true
  }' && echo " ✓ Acondicionador" || echo " ✗ Error"

# Pomada Fuerte
curl -X POST "$API_URL/items?page=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Pomada Fuerte",
    "description": "Pomada de fijación fuerte, acabado brillante, estilo clásico",
    "category": "PRODUCT",
    "active": true
  }' && echo " ✓ Pomada Fuerte" || echo " ✗ Error"

# Gel Styling
curl -X POST "$API_URL/items?page=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Gel Styling",
    "description": "Gel de fijación media, acabado natural sin brillo",
    "category": "PRODUCT",
    "active": true
  }' && echo " ✓ Gel Styling" || echo " ✗ Error"

# Spray Fijador
curl -X POST "$API_URL/items?page=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Spray Fijador",
    "description": "Spray para fijar peinado durante todo el día",
    "category": "PRODUCT",
    "active": true
  }' && echo " ✓ Spray Fijador" || echo " ✗ Error"

# Aceite para Barba
curl -X POST "$API_URL/items?page=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Aceite para Barba",
    "description": "Aceite hidratante y suavizante para barba, con aroma a madera",
    "category": "PRODUCT",
    "active": true
  }' && echo " ✓ Aceite para Barba" || echo " ✗ Error"

# Loción Aftershave
curl -X POST "$API_URL/items?page=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Loción Aftershave",
    "description": "Loción refrescante post-afeitado, desinfectante y aromática",
    "category": "PRODUCT",
    "active": true
  }' && echo " ✓ Loción Aftershave" || echo " ✗ Error"

# Tónico Capilar
curl -X POST "$API_URL/items?page=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Tónico Capilar",
    "description": "Tónico estimulante para crecimiento y fortaleza del cabello",
    "category": "PRODUCT",
    "active": true
  }' && echo " ✓ Tónico Capilar" || echo " ✗ Error"

# Mascarilla Capilar
curl -X POST "$API_URL/items?page=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Mascarilla Capilar",
    "description": "Mascarilla de tratamiento profundo para cabello seco o dañado",
    "category": "PRODUCT",
    "active": true
  }' && echo " ✓ Mascarilla Capilar" || echo " ✗ Error"

# Crema para Peinar
curl -X POST "$API_URL/items?page=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Crema para Peinar",
    "description": "Crema ligera para peinar sin necesidad de agua",
    "category": "PRODUCT",
    "active": true
  }' && echo " ✓ Crema para Peinar" || echo " ✗ Error"

# Colonia
curl -X POST "$API_URL/items?page=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Colonia Barber",
    "description": "Colonia clásica con aroma barbero, 100ml",
    "category": "PRODUCT",
    "active": true
  }' && echo " ✓ Colonia Barber" || echo " ✗ Error"

echo ""
echo "✅ Carga completada!"
