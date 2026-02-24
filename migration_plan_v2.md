# 🎨 RIXY ANDROID UX TRANSFORMATION PLAN V2

> **Objetivo:** Elevar la app Android al mismo nivel de sofisticación visual y experiencia de usuario que la app iOS de referencia, manteniendo los principios de Material3 pero adaptándolos al lenguaje visual distintivo de Rixy.

---

## 📊 ANÁLISIS COMPARATIVO: iOS vs Android Actual

| Aspecto | iOS (Referencia) | Android (Actual) | Prioridad |
|---------|------------------|------------------|-----------|
| **Sombras** | Soft, difuminadas (0.08 opacity, y:6, r:9) | Elevación Material3 estándar plana | 🔴 CRÍTICA |
| **Cards** | Border sutil + sombra soft + padding 24dp | Material3 Card genérica sin personalización | 🔴 CRÍTICA |
| **Inputs** | Glow focus state + stroke animado + 44dp height | OutlinedTextField básico de 56dp | 🔴 CRÍTICA |
| **Tipografía** | Inter custom con tracking ajustado | System font genérica | 🟡 ALTA |
| **Navegación** | Transiciones fluidas, gestures nativos | Cambios bruscos entre pantallas | 🔴 CRÍTICA |
| **Empty States** | Ilustraciones + iconos SF Symbols coherentes | Genéricos, poco pulidos | 🟡 ALTA |
| **Micro-interacciones** | Haptics + spring animations en todo | Sin feedback táctil significativo | 🔴 CRÍTICA |
| **Espaciado** | 8pt grid consistente (4, 8, 16, 24, 32, 48) | Variable, inconsistente | 🟡 ALTA |
| **Botones** | Scale animation 0.96x + shadow sutil | Elevación plana Material3 | 🔴 CRÍTICA |
| **Listing Cards** | Heart button overlay + aspect ratio 4:3 + fixed heights | Sin favoritos visible + alturas variables | 🔴 CRÍTICA |
| **Tab Bar** | Icon scale 1.1x en selección + sin indicador | Indicador Material3 por defecto | 🟡 ALTA |
| **Hero Section** | Gradient diagonal + stats en row | No existe equivalente | 🔴 CRÍTICA |

---

## 🎯 DIFERENCIAS CLAVE POR COMPONENTE

### 1. Sistema de Sombras

**iOS (DSCard.swift):**
```swift
.shadow(color: .black.opacity(0.08), radius: 9, x: 0, y: 6)
```

**Android Actual:**
```kotlin
// Usa elevation default de Material3 (sin difuminado real)
elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
```

**Gap:** iOS usa sombras difuminadas (blur real) mientras Android usa elevation plana. La percepción visual es significativamente diferente.

---

### 2. Cards

**iOS:**
- Padding interno: **24dp**
- Border radius: **16dp**
- Border stroke: **1dp** con color sutil
- Sombra: Custom con blur
- Background: Surface puro

**Android Actual:**
- Padding interno: **16dp** (estándar)
- Border radius: **12dp**
- Sin border stroke consistente
- Elevation plana Material3

---

### 3. Inputs (TextFields)

**iOS (DSTextField.swift):**
- Height: **44dp**
- Border radius: **8dp**
- Focus: Glow de 6dp con color brand (0.18 alpha)
- Stroke en focus: **2dp** con brand (0.4 alpha)
- Icono izquierdo: 16dp con color subtext
- Placeholder: Centrado verticalmente

**Android Actual:**
- Height: **56dp** (estándar Material3)
- Border radius: **8dp** ✅
- Focus: Sin glow, solo color change
- Stroke: 1-2dp sin animación fluida

---

### 4. Botones

**iOS (DSButton.swift):**
- Heights: 36dp (sm), 44dp (md/lg), 40dp (icon)
- Border radius: **10dp**
- Press animation: **Scale 0.96x** con spring
- Colores: Primary (Brand), Secondary (Monetization), Outline (con border 0.2 alpha)
- Icon + texto: Spacing 8dp

**Android Actual:**
- Heights: 36dp, 44dp, 48dp ❌ (lg diferente)
- Border radius: **10dp** ✅
- Press animation: Ripple genérico Material3
- Colores: Bien definidos pero sin variante LINK

---

### 5. Listing Cards

**iOS (ListingCard.swift):**
- Aspect ratio imagen: **4:3**
- Width fijo: **260dp** (carousel) / flexible (grid)
- Type badge: Capsule con icono emoji + texto
- Heart button: Overlay top-right con shadow
- Info section: Heights fijos (38dp título, 14dp business, 16dp price)
- Price: Brand color, fontWeight semibold

**Android Actual:**
- Aspect ratio: **4:3** ✅
- Width: 200dp ❌ (muy pequeño)
- Type badge: Rectangular sin icono
- Heart button: **No existe**
- Info section: Heights variables
- Price: Brand color ✅

---

### 6. City Hero Section

**iOS (CityHomeView.swift):**
- Gradient diagonal: Brand → Brand Light
- Border radius: **24dp**
- Stats: 3 columnas con valores grandes (22sp bold)
- Height: **200dp**
- Padding: 24dp

**Android Actual:**
- **No existe equivalente** ❌

---

### 7. Tab Bar

**iOS (UserTabBarView.swift):**
- Icono seleccionado: Scale **1.1x** con spring
- Color tint: Brand para seleccionado
- Sin indicador de selección (Material3 indicator removido)
- Transiciones fluidas entre tabs

**Android Actual:**
- Icono: Sin animación de escala
- Con indicador Material3 por defecto ❌
- Transiciones bruscas

---

## 🛠️ ARQUITECTURA DE IMPLEMENTACIÓN

### Estructura de Archivos Propuesta

```
app/src/main/java/com/externalpods/rixy/core/designsystem/
├── animations/
│   ├── RixyAnimations.kt          # Spring specs, tween easings
│   └── HapticFeedback.kt          # Extensiones para haptics
├── components/
│   ├── v2/                        # NUEVOS COMPONENTES iOS-Style
│   │   ├── DSCard.kt
│   │   ├── DSButton.kt
│   │   ├── DSTextField.kt
│   │   ├── DSListingCard.kt
│   │   ├── DSSectionHeader.kt
│   │   ├── DSTypeBadge.kt
│   │   ├── DSHeroSection.kt
│   │   ├── DSCategoryCard.kt
│   │   ├── DSSkeleton.kt
│   │   └── DSNavigationBar.kt
│   └── legacy/                    # Componentes actuales (deprecated)
├── modifiers/
│   ├── ShadowModifiers.kt         # iosShadow(), cardShadow()
│   ├── PressModifiers.kt          # iosPressAnimation()
│   └── FocusModifiers.kt          # focusGlow()
├── theme/
│   ├── RixyColors.kt              # (existente - ajustes menores)
│   ├── RixyTypography.kt          # AGREGAR: Inter font family
│   ├── RixyShapes.kt              # (existente - validar)
│   ├── RixyShadows.kt             # NUEVO: Sistema de sombras
│   └── RixySpacing.kt             # (existente - validar)
└── utils/
    ├── RenderEffectUtils.kt       # Blur effects API 31+
    └── AnimationUtils.kt          # Helpers de animación
```

---

## 📋 CHECKLIST DETALLADO DE IMPLEMENTACIÓN

### FASE 1: Fundamentos del Sistema (Días 1-3)

#### 1.1 Sistema de Sombras iOS-Style
- [ ] Crear `RixyShadows.kt` con tokens de sombra
  - [ ] `Card`: color=0.08 black, y=6, blur=9
  - [ ] `Modal`: color=0.12 black, y=10, blur=14
  - [ ] `Elevated`: color=0.12 black, y=8, blur=16
  - [ ] `Pressed`: color=0.04 black, y=2, blur=4
- [ ] Implementar `Modifier.iosShadow()` con `drawBehind`
- [ ] Implementar fallback para API < 31 sin RenderEffect
- [ ] Validar visualmente contra capturas de iOS

#### 1.2 Animaciones y Haptics
- [ ] Crear `RixyAnimations.kt`
  - [ ] `SpringDefault`: stiffness=300, damping=0.8
  - [ ] `SpringBounce`: stiffness=400, damping=0.6
  - [ ] `SpringGentle`: stiffness=200, damping=0.9
  - [ ] `EaseOut`: CubicBezier(0.25, 0.1, 0.25, 1)
- [ ] Crear `HapticFeedback.kt`
  - [ ] `performTapFeedback()`
  - [ ] `performSuccessFeedback()`
  - [ ] `performErrorFeedback()`

#### 1.3 Tipografía Inter
- [ ] Descargar fuentes Inter (Regular, Medium, SemiBold, Bold)
- [ ] Agregar a `res/font/`
- [ ] Crear `FontFamily.Inter`
- [ ] Actualizar `RixyTypography` para usar Inter
- [ ] Validar tracking/letterSpacing contra iOS

---

### FASE 2: Componentes Core (Días 4-7)

#### 2.1 DSCard (Reemplazo de RixyCard)
- [ ] Estructura base con `Box` (no Material3 Card)
- [ ] Implementar sombra iOS con `Modifier.iosShadow()`
- [ ] Border stroke: 1dp con `RixyColors.Border`
- [ ] Padding interno: **24dp** (no 16dp)
- [ ] Border radius: **16dp**
- [ ] Background: `RixyColors.Surface`
- [ ] Click handling sin ripple Material3 (o ripple sutil)
- [ ] Estados: Default, Pressed (scale 0.98x)
- [ ] Variantes: Elevated, Outlined, Filled

#### 2.2 DSButton (Reemplazo de RixyButton)
- [ ] Alturas correctas: 36dp (sm), 44dp (md/lg), 40dp (icon)
- [ ] Border radius: **10dp**
- [ ] Scale animation: **0.96x** on press con spring
- [ ] Padding horizontal: 12dp (sm), 20dp (md), 32dp (lg)
- [ ] Variantes:
  - [ ] `Primary`: Brand background, white text
  - [ ] `Secondary`: Structure background, white text
  - [ ] `Outline`: Transparent, border 1dp (0.2 alpha structure)
  - [ ] `Ghost`: Transparent, structure text
  - [ ] `Link`: Transparent, brand text, no background
  - [ ] `Destructive`: Error background
- [ ] Icon support: Start position, 16dp size, 8dp spacing
- [ ] Loading state: ProgressIndicator con color correcto
- [ ] Disabled state: Opacity 0.5 o background gris
- [ ] Haptic feedback on click

#### 2.3 DSTextField (Reemplazo de RixyTextField)
- [ ] Height: **44dp** (no 56dp)
- [ ] Border radius: **8dp**
- [ ] Background: `RixyColors.Surface`
- [ ] Border:
  - [ ] Unfocused: 1dp `RixyColors.Border`
  - [ ] Focused: 2dp `RixyColors.Brand.copy(alpha=0.4)`
  - [ ] Error: 2dp `RixyColors.Error`
- [ ] Focus glow: Shadow con `RixyColors.Brand.copy(alpha=0.18)`
- [ ] Icono izquierdo: 16dp, `RixyColors.TextSecondary`
- [ ] Placeholder: `RixyTypography.Body`, `RixyColors.TextTertiary`
- [ ] Text input: `RixyTypography.Body`, `RixyColors.TextPrimary`
- [ ] Animation: Border width y color con spring
- [ ] Cursor: `RixyColors.Brand`
- [ ] Variantes: Password (con visibility toggle), Search (con clear)

#### 2.4 DSSectionHeader
- [ ] Layout: HStack con VStack para título/subtítulo
- [ ] Title: `RixyTypography.H2` (22sp bold)
- [ ] Subtitle: `RixyTypography.Subtext` (14sp)
- [ ] "Ver más" action: `RixyTypography.Caption`, Brand color
- [ ] Icono flecha opcional
- [ ] Spacing: 4dp entre título y subtítulo

---

### FASE 3: Cards y Listados (Días 8-11)

#### 3.1 DSListingCard (Horizontal/Carousel)
- [ ] Width fijo: **260dp**
- [ ] Aspect ratio imagen: **4:3** (195dp height)
- [ ] Sombra iOS aplicada al card completo
- [ ] Imagen con `ContentScale.Crop`
- [ ] Type badge: Capsule con emoji + texto
  - [ ] Background: Color tipo con 0.9 alpha
  - [ ] Position: Top-start con 12dp padding
  - [ ] Border radius: 4dp
- [ ] Heart button:
  - [ ] Position: Top-end con 8dp padding
  - [ ] Icon: Filled/Outlined según estado
  - [ ] Color: White (outline) o Error (filled)
  - [ ] Shadow para visibilidad sobre imagen
  - [ ] Click con haptic
- [ ] Info section:
  - [ ] Height fija: **100dp**
  - [ ] Padding: 16dp horizontal, 12dp vertical
  - [ ] Título: 38dp height, 2 líneas max, `RixyTypography.Body`
  - [ ] Business name: 14dp height, 1 línea, `RixyTypography.Caption`
  - [ ] Price: 16dp height, `RixyTypography.Caption` + Semibold, Brand color
  - [ ] Placeholder " " para mantener estructura cuando no hay datos

#### 3.2 DSListingCardCompact (Vertical List)
- [ ] Layout: HStack (Row)
- [ ] Thumbnail: 80dp x 80dp, border radius 8dp
- [ ] Info: Column con TypeBadgeSmall, título, precio
- [ ] Chevron: Right-aligned, `RixyColors.TextSecondary` 0.6 alpha
- [ ] Background: `RixyColors.Background`
- [ ] Border radius: **16dp**

#### 3.3 DSHeroSlotCard
- [ ] Width: Full width menos padding 16dp
- [ ] Height total: 290dp (200dp imagen + 90dp info)
- [ ] Imagen:
  - [ ] Height: 200dp
  - [ ] Type badge: Capsule style con fondo negro 0.6 alpha
  - [ ] Border radius solo arriba: 16dp
- [ ] Info section:
  - [ ] Height fija: 90dp
  - [ ] Padding: 16dp horizontal, 12dp vertical
  - [ ] Título: 20sp semibold, 1 línea
  - [ ] Business: 14sp, subtext color
  - [ ] Price: Brand color, semibold
- [ ] Sombra iOS completa

#### 3.4 DSCategoryCard
- [ ] Grid: 2 columnas
- [ ] Height: **120dp**
- [ ] Background: Color categoría con 0.1 alpha
  - [ ] Productos: Blue
  - [ ] Servicios: Purple
  - [ ] Eventos: Pink
  - [ ] Negocios: Orange
- [ ] Icono: Emoji 40sp
- [ ] Texto: 16sp, `RixyTypography.Body`
- [ ] Border radius: **16dp**
- [ ] Sin sombra (flat design)

---

### FASE 4: Pantallas Principales (Días 12-16)

#### 4.1 CityHomeScreen Refactor
- [ ] **CityHeroSection:**
  - [ ] Gradient diagonal Brand → Brand Light
  - [ ] Border radius: 24dp
  - [ ] Height: 200dp
  - [ ] Padding: 24dp
  - [ ] Ciudad nombre: `RixyTypography.H1`, white
  - [ ] Location: `RixyTypography.Body`, white 0.9 alpha
  - [ ] Stats row: 3 items con 32dp spacing
    - [ ] Valor: 22sp bold, white
    - [ ] Label: `RixyTypography.Caption`, white 0.8 alpha
- [ ] **CategoryGridSection:**
  - [ ] Header: "Explorar", `RixyTypography.H2`
  - [ ] Grid: 2x2 con 16dp spacing
  - [ ] Cards: DSCategoryCard
- [ ] **SlotSectionView:**
  - [ ] Header: Con "Ver más" si hay múltiples slots
  - [ ] Single slot: DSHeroSlotCard full width
  - [ ] Multiple slots: Horizontal scroll, DSListingCard 260dp
- [ ] **FeaturedSection:**
  - [ ] Header: "Destacado"
  - [ ] Card: FeaturedListingCard (variante grande)
- [ ] **FeedSection:**
  - [ ] Header: "Recientes" con "Ver más"
  - [ ] List: Vertical con DSListingCardCompact
  - [ ] Máximo 6 items
- [ ] **BusinessCTASection:**
  - [ ] Background: `RixyColors.Background`
  - [ ] Border radius: 16dp
  - [ ] Título: 20sp semibold
  - [ ] Subtítulo: 14sp subtext
  - [ ] Botón: DSButton Large Primary

#### 4.2 TabBar Refactor
- [ ] DSNavigationBar:
  - [ ] Container color: `RixyColors.Surface`
  - [ ] Sin elevation (0dp)
  - [ ] Sin indicador de selección Material3
  - [ ] Iconos: Filled cuando seleccionado, Outlined cuando no
  - [ ] Animación: Scale 1.1x en icono seleccionado
  - [ ] Color: Brand cuando seleccionado, TextSecondary cuando no
  - [ ] Labels siempre visibles

#### 4.3 LoginScreen Refactor
- [ ] Header:
  - [ ] Icono: Key o Logo 80dp, Brand color
  - [ ] Título: "KeyCity", `RixyTypography.H1`
  - [ ] Subtítulo: "Inicia sesión para continuar"
- [ ] Form:
  - [ ] DSTextField email con icono envelope
  - [ ] DSTextField password con icono lock
  - [ ] Error message: `RixyTypography.Caption`, Error color
  - [ ] DSButton Large Primary "Iniciar sesión"
- [ ] Footer:
  - [ ] "¿No tienes cuenta?" + DSButton Link "Regístrate"
- [ ] Layout: ScrollView con centrado vertical

---

### FASE 5: Navegación y Transiciones (Días 17-19)

#### 5.1 Transiciones de Navegación
- [ ] Crear `RixyNavigationTransitions.kt`
- [ ] Slide horizontal (iOS style):
  - [ ] Enter: Slide from right + fade in
  - [ ] Exit: Slide to left + fade out
  - [ ] Pop enter: Slide from left + fade in
  - [ ] Pop exit: Slide to right + fade out
- [ ] Fade vertical (modales):
  - [ ] Enter: Slide from bottom + fade
  - [ ] Exit: Slide to bottom + fade
- [ ] Shared element transitions (opcional avanzado)

#### 5.2 Pull-to-Refresh
- [ ] Custom indicator con brand color
- [ ] Animation: Scale + rotation
- [ ] Sin bounce effect exagerado de Material3

---

### FASE 6: Estados y Micro-interacciones (Días 20-22)

#### 6.1 Skeleton Views
- [ ] DSSkeletonCard
  - [ ] Shimmer effect con gradient
  - [ ] Match dimensions de cards reales
- [ ] DSSkeletonText
  - [ ] Heights: 24dp (título), 16dp (body), 12dp (caption)
- [ ] DSSkeletonImage
  - [ ] Aspect ratios: 4:3, 16:9, 1:1

#### 6.2 Empty States
- [ ] DSEmptyState
  - [ ] Icono: 80dp, `RixyColors.TextTertiary`
  - [ ] Título: `RixyTypography.H3`
  - [ ] Mensaje: `RixyTypography.Body`, TextSecondary
  - [ ] Action: DSButton opcional

#### 6.3 Error States
- [ ] DSErrorView
  - [ ] Icono: Alert triangle
  - [ ] Mensaje de error
  - [ ] Botón "Reintentar"

#### 6.4 Loading States
- [ ] Progress indicators: Brand color
- [ ] Full screen loading: Con logo animado (opcional)
- [ ] Inline loading: Skeletons preferidos

---

### FASE 7: Testing y Polish (Días 23-25)

#### 7.1 Testing Visual
- [ ] Comparar lado-a-lado con app iOS en dispositivo físico
- [ ] Verificar sombras en diferentes fondos
- [ ] Verificar contraste y accesibilidad
- [ ] Testear en dark mode

#### 7.2 Performance
- [ ] Eliminar recomposiciones innecesarias
- [ ] Optimizar imágenes (Coil config)
- [ ] Lazy loading en listas
- [ ] Profile de animaciones

#### 7.3 Accessibility
- [ ] Content descriptions en todos los iconos
- [ ] Touch targets mínimo 44dp
- [ ] Screen reader testing
- [ ] High contrast mode

---

## 📁 COMPONENTES LEGACY VS NUEVOS

| Componente Actual | Nuevo Componente | Estado |
|-------------------|------------------|--------|
| `RixyCard` | `DSCard` | 🔄 Reemplazar |
| `RixyButton` | `DSButton` | 🔄 Reemplazar |
| `RixyTextField` | `DSTextField` | 🔄 Reemplazar |
| `ListingCard` | `DSListingCard` | 🔄 Reemplazar |
| `SectionHeader` | `DSSectionHeader` | 🔄 Reemplazar |
| `RixyBadge` | `DSTypeBadge` | 🔄 Reemplazar |
| `CityCard` | `DSHeroSection` | 🔄 Reemplazar |
| `SearchBar` | `DSSearchField` | 🔄 Reemplazar |
| `SkeletonView` | `DSSkeleton` | 🔄 Reemplazar |
| `EmptyStateView` | `DSEmptyState` | 🔄 Reemplazar |
| `UserTabBar` | `DSNavigationBar` | 🔄 Reemplazar |

---

## 🔧 DECISIONES TÉCNICAS

### RenderEffect vs Canvas Shadows
- **API 31+**: Usar `RenderEffect.createBlurEffect()` para sombras reales
- **API < 31**: Usar `drawBehind` con múltiples capas de degradado para simular blur

### Animaciones
- **Compose Animation API**: `animate*AsState` con springs
- **Easing**: Curvas CubicBezier que repliquen iOS
- **Duration**: 150-200ms para micro-interacciones

### Fonts
- **Inter**: Descargar desde Google Fonts
- **Fallback**: System font si Inter no está disponible
- **Weights**: Regular (400), Medium (500), SemiBold (600), Bold (700)

### Haptics
- **Click**: `HapticFeedbackType.TextHandleMove` (ligero)
- **Success**: `HapticFeedbackType.LongPress` (más fuerte)
- **Error**: `HapticFeedbackType.Reject` (pattern)

---

## ✅ CRITERIOS DE ACEPTACIÓN

### Paridad Visual
- [ ] Usuario no puede distinguir screenshots de iOS vs Android
- [ ] Sombras se ven igual de suaves en ambas plataformas
- [ ] Tipografía tiene el mismo peso visual
- [ ] Espaciado y proporciones son idénticos

### Interacción
- [ ] Botones responden con la misma física (spring)
- [ ] Inputs tienen el mismo feedback visual
- [ ] Transiciones son igual de fluidas
- [ ] Haptics proporcionan el mismo feedback táctil

### Performance
- [ ] 60fps en todas las animaciones
- [ ] Listas scroll a 60fps con 100+ items
- [ ] Cold start < 2 segundos
- [ ] APK size incremento < 500KB

### Accessibility
- [ ] TalkBack funciona correctamente
- [ ] Contraste WCAG AA en todos los textos
- [ ] Touch targets accesibles

---

## 🚀 PROXIMOS PASOS

**¿Por dónde quieres que empecemos?**

### Opción A: Fundamentos Primero (Recomendado)
Empezar por Fase 1 y 2 para establecer la base del sistema de diseño. Esto permite que otros desarrolladores usen los nuevos componentes mientras seguimos con las pantallas.

### Opción B: Pantalla Crítica Primero
Ir directo a la `CityHomeScreen` refactor completa para tener un "wow" inmediato y visible. Luego extrapolamos el sistema.

### Opción C: Componente por Componente
Crear todos los componentes de la Fase 2 y 3 primero, luego integrarlos en las pantallas.

### Opción D: Shadow System First
Solo implementar el sistema de sombras primero como POC visual, luego seguimos con el resto.

---

**Escribe el número de la opción o dime "Empezar con [X]" y comenzamos inmediatamente.**
