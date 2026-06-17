# 🛒 Super Ahorro
**Trabajo Práctico Integrador — Tecnologías Móviles 2026**
**UNDEF — Grupo: BossioCorrea**

> Aplicación Android para registrar, consultar y analizar gastos de supermercado. Permite escanear tickets con IA, comparar precios entre supermercados, ver promociones reales y consultar el historial de compras mediante un asistente conversacional.


## 🏗️ Arquitectura

El proyecto sigue el patrón **MVVM (Model - View - ViewModel)** con una estructura de capas clara:

```
com.undef.superahorro.BossioCorrea/
│
├── data/
│   ├── local/              → SessionManager y NotificationsPreferences (DataStore)
│   ├── mock/                → Datos de ejemplo para previews
│   └── repository/          → AuthRepository (Firebase Auth + perfil en Firestore)
│                               CompraRepository (compras/productos en Firestore)
│                               GroqRepository (OCR de tickets y chat con IA)
│                               PromocionesRepository (catálogos públicos VTEX)
│
├── domain/
│   └── model/               → Modelos de datos (Compra, Producto, Usuario, PrecioProducto)
│
├── ui/
│   ├── components/          → Componentes reutilizables (TopBar, BottomBar, etc.)
│   ├── navegation/           → NavGraph, Routes, UiState
│   ├── screens/              → Pantallas organizadas por feature
│   │   ├── splash/
│   │   ├── login/            → Login con email/contraseña + biometría (huella/rostro)
│   │   ├── register/
│   │   ├── olvidopassword/
│   │   ├── home/
│   │   ├── compras/
│   │   │   ├── nueva/        → Alta de compra + escaneo de ticket con cámara y IA
│   │   │   ├── listado/
│   │   │   ├── detalle/
│   │   │   └── historial/    → Historial agrupado + exportar a CSV/PDF
│   │   ├── productos/
│   │   ├── estadisticas/
│   │   ├── comparativa/      → Comparativa de precios entre supermercados
│   │   ├── promociones/      → Promociones reales (APIs públicas)
│   │   ├── chat/              → Asistente IA sobre el historial de compras
│   │   ├── perfil/
│   │   └── settings/
│   └── theme/                → Color, Type, Theme (Material3), idioma y tema
│
├── util/                     → ExportUtils (generación de CSV/PDF)
└── MainActivity.kt
```

---

## ✅ Pantallas implementadas

| Pantalla | Descripción |
|---|---|
| **Splash** | Bienvenida con logo, gradiente verde y acceso a login/registro |
| **Login** | Email/contraseña con validación, mostrar/ocultar contraseña y acceso por **huella/biometría** |
| **Registro** | Creación de cuenta en Firebase Auth con confirmación de contraseña |
| **Olvidé mi contraseña** | Recuperación por email real (Firebase Auth) |
| **Home** | Resumen del mes, gasto total, mini chart semanal, acciones rápidas y notificaciones de promociones |
| **Nueva Compra** | Formulario con supermercado, fecha, hora y total — incluye **foto del ticket + análisis automático con IA** |
| **Nuevo Producto** | Agregar producto con código, nombre, cantidad, precio y categoría |
| **Listado de Compras** | Lista con búsqueda, filtros, swipe-to-delete y hero card de resumen |
| **Detalle de Compra** | Tabla de productos, totales, editar y eliminar |
| **Historial de Compras** | Compras agrupadas por mes, con filtros y **exportación a CSV/PDF** (compartir por Intent) |
| **Estadísticas** | Gráficos de barras, KPIs, gasto por período y por supermercado |
| **Comparativa de Precios** | Compara el precio del mismo producto entre supermercados durante un mes |
| **Promociones** | Ofertas reales obtenidas de los catálogos públicos de Carrefour y Changomás |
| **Chat (Asistente IA)** | Consultas en lenguaje natural sobre el propio historial de compras |
| **Mi Perfil** | Edición de datos personales (Firestore) |
| **Configuración** | Notificaciones, idioma, tema claro/oscuro/sistema, moneda y cierre de sesión |

---

## ✨ Funcionalidades destacadas

- **Autenticación real** con Firebase Auth (email/contraseña, recuperación de contraseña) + **login biométrico** (huella/rostro) usando `androidx.biometric`.
- **Persistencia en la nube** con Firestore: compras, productos y perfil de usuario, con caché offline automática.
- **Sesión persistente** con Jetpack DataStore (`SessionManager`), evita tener que loguearse cada vez.
- **Escaneo de tickets con IA**: se toma una foto con la cámara y un modelo de visión (Groq) completa automáticamente supermercado, fecha, hora, total y la lista de productos (con manejo de descuentos).
- **Chat con IA** sobre el historial: el asistente responde preguntas sobre las compras propias del usuario.
- **Comparativa de precios**: detecta cuándo el mismo producto se compró en distintos supermercados en un mismo mes y compara precios.
- **Promociones reales**: consulta las APIs públicas (VTEX) de Carrefour y Changomás para mostrar ofertas vigentes.
- **Exportar y compartir**: genera CSV y PDF del historial de compras y los comparte vía `Intent`/`FileProvider`.
- **Notificaciones configurables** de promociones (DataStore).
- **Internacionalización** (ES/EN) y tema claro/oscuro/según el sistema.

---

## 🛠️ Tecnologías utilizadas

| Tecnología | Uso |
|---|---|
| **Kotlin** | Lenguaje principal |
| **Jetpack Compose** | UI declarativa |
| **Navigation Compose** | Navegación entre pantallas |
| **ViewModel + StateFlow** | Patrón MVVM, manejo de estado |
| **Coroutines** | Operaciones asincrónicas (Firestore, networking) |
| **Material3** | Diseño visual, tema claro/oscuro |
| **Material Icons Extended** | Íconos adicionales |
| **Firebase Auth** | Autenticación de usuarios |
| **Firebase Firestore** | Base de datos en la nube (compras, productos, perfil) |
| **Jetpack DataStore** | Sesión de usuario y preferencias de notificaciones |
| **AndroidX Biometric** | Login con huella digital / reconocimiento facial |
| **OkHttp + Groq API** | OCR de tickets (modelo de visión) y chat IA sobre el historial |
| **APIs públicas VTEX** | Promociones reales de supermercados argentinos |
| **Coil** | Carga de la imagen del ticket |
| **CameraX / ActivityResult (TakePicture) + FileProvider** | Captura de fotos del ticket y compartir archivos exportados |

---

## 📦 Requisitos del sistema

- **Android Studio**: Hedgehog o superior
- **Kotlin**: 2.0.21
- **Compile SDK**: 35
- **Min SDK**: 26 (Android 8.0 Oreo)
- **Target SDK**: 35
- **Proyecto Firebase**: el repo ya incluye `app/google-services.json` configurado
- **(Opcional) API key de Groq**: necesaria para el escaneo de tickets con IA y el chat del historial

---

## 🚀 Cómo correr el proyecto

1. Clonar el repositorio:
```bash
git clone https://github.com/franbossio/SuperAhorro_TecnologiasMoviles.git
```

2. (Opcional, para usar el escaneo de tickets con IA y el chat) crear un archivo `local.properties` en la raíz del proyecto con:
```properties
GROQ_API_KEY=tu_api_key_de_groq
```
> Sin esta clave, el resto de la app funciona normalmente (auth, compras, listado, historial, estadísticas, comparativa, promociones, exportar); solo el escaneo automático de tickets y el chat IA quedarán deshabilitados.

3. Abrir el proyecto en **Android Studio** y sincronizar Gradle (`File → Sync Project with Gradle Files`). `google-services.json` ya está incluido en el repo, no es necesario agregarlo.

4. Correr en emulador o dispositivo físico con Android 8.0+.
   - Para el login biométrico, el dispositivo/emulador debe tener una huella o rostro configurado.
   - Para escanear tickets, otorgar el permiso de cámara cuando la app lo solicite.

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

### Segunda entrega — 05/06/2026 ✅
- [x] Persistencia en la nube con Firebase Firestore (compras y productos)
- [x] Sesión de usuario con DataStore (reemplaza Room/SharedPreferences)
- [x] Networking — Groq API (OCR/chat) y APIs públicas VTEX (promociones)
- [x] Corrutinas en operaciones reales (Firestore, networking)
- [x] Intent para compartir (exportar CSV/PDF) y cámara para escanear tickets
- [x] Menús y diálogos (confirmaciones, configuración)

### Funcionalidades adicionales ✅
- [x] Login biométrico (huella / rostro)
- [x] Escaneo de tickets con IA (OCR + carga automática de productos)
- [x] Chat con IA sobre el historial de compras
- [x] Comparativa de precios entre supermercados
- [x] Promociones reales (Carrefour, Changomás)
- [x] Notificaciones de promociones configurables

---

## 🗂️ Estructura de navegación

```
Splash
 ├── Login (email/contraseña o huella)
 │    └── Olvidé mi contraseña
 └── Registro
      └── Home
           ├── Nueva Compra (cámara + OCR con IA) → Nuevo Producto
           ├── Listado de Compras → Detalle de Compra
           ├── Historial de Compras (exportar CSV/PDF) → Detalle de Compra
           ├── Estadísticas → Comparativa de Precios
           ├── Promociones
           ├── Chat (Asistente IA)
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

## Presentacion Segunda Entrega

https://docs.google.com/presentation/d/1II9rcRuCFt1JghjPA8PojKF9xPl8iLWJ/edit?usp=sharing&ouid=113337839765885953025&rtpof=true&sd=true

## Video de la app 

https://drive.google.com/file/d/1NmVdKSoue2FrB_42BJWcu4E3Bf-ju0Sj/view?usp=sharing


