# 🛒 Super Ahorro
**Trabajo Práctico Integrador — Tecnologías Móviles 2026**
**UNDEF — Grupo: BossioCorrea**

> Aplicación Android para registrar, consultar y analizar gastos de supermercado, permitiendo llevar un mejor control de las compras y detectar oportunidades de ahorro.


## 🏗️ Arquitectura

El proyecto sigue el patrón **MVVM (Model - View - ViewModel)** con una estructura de capas clara:

```
com.undef.superahorro.BossioCorrea/
│
├── data/
│   └── mock/               → Datos mockeados para la primera entrega
│
├── domain/
│   └── model/              → Modelos de datos (Compra, Producto, Usuario)
│
├── ui/
│   ├── components/         → Componentes reutilizables (TopBar, LabelCaps, etc.)
│   ├── navegation/         → NavGraph, Routes, UiState
│   ├── screens/            → Pantallas organizadas por feature
│   │   ├── splash/
│   │   ├── login/
│   │   ├── register/
│   │   ├── home/
│   │   ├── compras/
│   │   │   ├── nueva/
│   │   │   ├── listado/
│   │   │   ├── detalle/
│   │   │   └── historial/
│   │   ├── productos/
│   │   ├── estadisticas/
│   │   ├── perfil/
│   │   └── settings/
│   └── theme/              → Color, Type, Theme (Material3)
│
└── MainActivity.kt
```

---

## ✅ Pantallas implementadas

| Pantalla | Descripción |
|---|---|
| **Splash** | Bienvenida con logo, gradiente verde y acceso a login/registro |
| **Login** | Inicio de sesión con validación, mostrar/ocultar contraseña |
| **Registro** | Creación de cuenta con confirmación de contraseña |
| **Olvidé mi contraseña** | Flujo de recuperación por email con estado enviado/pendiente |
| **Home** | Resumen del mes, gasto total, mini chart semanal, acciones rápidas |
| **Nueva Compra** | Formulario con dropdown de supermercados, fecha, hora y total |
| **Nuevo Producto** | Agregar producto con código, nombre, cantidad, precio y categoría |
| **Listado de Compras** | Lista con búsqueda, filtros, swipe-to-delete y hero card de resumen |
| **Detalle de Compra** | Tabla de productos, totales, editar y eliminar |
| **Historial de Compras** | Compras agrupadas por mes con totales y filtros |
| **Estadísticas** | Gráficos de barras, KPIs, gasto por período y por supermercado |
| **Mi Perfil** | Edición de datos personales y supermercados favoritos |
| **Configuración** | Notificaciones, privacidad, idioma, moneda y cierre de sesión |

---

## 🛠️ Tecnologías utilizadas

| Tecnología | Uso |
|---|---|
| **Kotlin** | Lenguaje principal |
| **Jetpack Compose** | UI declarativa |
| **Navigation Compose** | Navegación entre pantallas |
| **ViewModel + StateFlow** | Patrón MVVM, manejo de estado |
| **Coroutines** | Operaciones asincrónicas (simuladas con `delay`) |
| **Material3** | Diseño visual, tema claro/oscuro |
| **Material Icons Extended** | Íconos adicionales |

---

## 📦 Requisitos del sistema

- **Android Studio**: Hedgehog o superior
- **Kotlin**: 2.0.21
- **Compile SDK**: 35
- **Min SDK**: 26 (Android 8.0 Oreo)
- **Target SDK**: 35

---

## 🚀 Cómo correr el proyecto

1. Clonar el repositorio:
```bash
git clone https://github.com/franbossio/SuperAhorro_TecnologiasMoviles.git
```

2. Abrir en **Android Studio**.

3. Sincronizar Gradle (`File → Sync Project with Gradle Files`).

4. Correr en emulador o dispositivo físico con Android 8.0+.

> **Nota:** En la primera entrega los datos son estáticos/mockeados. La persistencia real con Room, DataStore y Networking se incorpora en la segunda entrega.

---

## 📋 Estado de las entregas

### Primera entrega — 08/05/2026 ✅
- [x] Todas las pantallas principales implementadas
- [x] Navegación entre pantallas con Navigation Compose
- [x] Datos mockeados
- [x] Arquitectura MVVM base
- [x] Tema visual consistente (paleta verde esmeralda)
- [x] Internacionalización con `strings.xml` (ES / EN)
- [x] Paquete: `com.undef.superahorro.BossioCorrea`

### Segunda entrega — 05/06/2026 🔜
- [ ] Persistencia local con Room (compras y productos)
- [ ] Sesión de usuario con DataStore / SharedPreferences
- [ ] Networking — GET y POST a API externa
- [ ] Corrutinas en operaciones reales
- [ ] Intent para compartir compra y abrir cámara/galería
- [ ] Menús y diálogos

### Entrega final 🔜
- [ ] Funcionalidades opcionales (OCR, chat IA, notificaciones, etc.)

---

## 🗂️ Estructura de navegación

```
Splash
 ├── Login
 │    └── Olvidé mi contraseña
 └── Registro
      └── Home
           ├── Nueva Compra → Nuevo Producto
           ├── Listado de Compras → Detalle de Compra
           ├── Historial de Compras → Detalle de Compra
           ├── Estadísticas
           ├── Mi Perfil
           └── Configuración
```

---

## 👥 Integrantes

Nombre 
| Bossio, Francisco  
| Correa, Sofia  

## Presentacion Primera Entrega

https://docs.google.com/presentation/d/1mYGUkIa217INziN8BKaNFLbPrw6QL1Of/edit?usp=sharing&ouid=113337839765885953025&rtpof=true&sd=true
