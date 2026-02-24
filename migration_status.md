# Rixy Android — iOS Migration Status & Architectural Plan

> **Source:** iOS Reference App "KeyCity" (`/ios_reference/`)
> **Target:** Android App "Rixy" (`com.externalpods.rixy`)
> **Architecture Guide:** `docs/ANDROID_APP_ARCHITECTURE.md`
> **Last Updated:** 2026-02-24
> **Audit Status:** Complete iOS vs Android parity analysis performed

---

## Overall Progress

| Phase | Name | Status | Approved |
|-------|------|--------|----------|
| 1 | Architectural Analysis & Discovery | ✅ Complete | ✅ |
| 2 | Data & Domain Layers (The Core) | ✅ Complete | ✅ |
| 3 | Presentation Layer (ViewModels) | ✅ Complete | ✅ |
| 4 | UI Layer (Core Screens) | 🚧 Partial | ⬜ |

---

## PHASE 1: Architectural Analysis & Discovery ✅

### 1.1 iOS App Summary

- **App Name:** KeyCity (iOS) → Rixy (Android)
- **Total:** 92 Swift files, 8000+ LOC, 7 markdown docs
- **Architecture:** MVVM + Clean Architecture, SwiftUI, @Observable, async/await
- **3 Modes:** User (consumer), Owner (seller), Admin (platform operator)
- **Backend:** Custom REST API + Supabase Auth + Stripe Payments

### 1.2 Module Decision

**Single `:app` module with strict package-level separation.**

### 1.3 Android Package Structure

```
com.externalpods.rixy/
├── RixyApplication.kt
├── MainActivity.kt
├── core/
│   ├── model/
│   ├── network/
│   ├── common/
│   └── designsystem/
├── data/
├── domain/
├── service/
├── feature/
│   ├── auth/
│   ├── user/
│   ├── owner/
│   ├── admin/
│   └── settings/
└── navigation/
```

---

## PHASE 2: Data & Domain Layers (The Core) ✅

All data layer components implemented:
- 10 Core Models with Kotlin Serialization
- 3 API Services (Public, Owner, Admin)
- 5 Repositories with full CRUD
- 15+ Use Cases
- 4 Services (Auth, Payment, ImageUpload, Analytics)
- Koin DI modules

---

## PHASE 3: Presentation Layer (ViewModels) ✅

All 16 ViewModels implemented with StateFlow:

**User Mode:**
- CitySelectorViewModel, CityHomeViewModel, ListingDetailViewModel
- BusinessProfileViewModel, BrowseListingsViewModel

**Owner Mode:**
- OwnerDashboardViewModel, BusinessEditorViewModel
- ListingEditorViewModel, FeaturedCampaignsViewModel, OwnerCitySlotsViewModel

**Admin Mode:**
- AdminDashboardViewModel, CitiesManagementViewModel
- ModerationViewModel, UsersManagementViewModel, AuditLogViewModel

**Auth & Settings:**
- LoginViewModel, RegisterViewModel, SettingsViewModel

---

## PHASE 4: UI Layer (Complete Implementation) 🚧

> **Status:** 🚧 Partial — Core Screens Done, Missing Editor UIs
> **Goal:** Complete ALL screens to match iOS 100%

### 4.1 Design System ✅ COMPLETE

**Theme:**
- [x] `Color.kt` — RixyColors with Brand (#E61E4D), Structure, Action
- [x] `Type.kt` — H1-H4, Body, Button, Caption, Price styles
- [x] `Shape.kt` — small (8dp), medium (12dp), large (16dp), extraLarge (24dp)
- [x] `Spacing.kt` — xs, sm, md, lg, xl, xxl
- [x] `Theme.kt` — RixyTheme composable

**Components (22 total):**
- [x] `RixyButton.kt`, `RixyTextField.kt`, `RixyCard.kt`
- [x] `ListingCard.kt`, `ListingCardHorizontal.kt`
- [x] `CityCard.kt`, `CityCardCompact.kt`
- [x] `StatusBadge.kt`, `ListingStatusBadge.kt`, `ModerationStatusBadge.kt`, `PaymentStatusBadge.kt`
- [x] `SectionHeader.kt`, `SearchBar.kt`, `SearchBarCompact.kt`
- [x] `SkeletonView.kt` with shimmer
- [x] `EmptyStateView.kt`, `EmptySearchResults.kt`, `EmptyListings.kt`, `EmptyErrorState.kt`
- [x] `RixyBadge.kt`

### 4.2 User Mode Screens ✅ COMPLETE

**Core Screens (5 implemented):**
- [x] `CitySelectorScreen.kt` — City grid with search
- [x] `CityHomeScreen.kt` — Dynamic sections, hero, feed
- [x] `ListingDetailScreen.kt` — Image carousel, pricing, contact
- [x] `BusinessProfileScreen.kt` — Business info, listings
- [x] `BrowseListingsScreen.kt` — Search, filters, infinite scroll

### 4.3 Owner Mode Screens 🚧 PENDING (4 screens)

> **Note:** ViewModels exist, UI screens missing

- [ ] `BusinessEditorScreen.kt`
  - Form: name, description, address, phone, whatsapp, website
  - Logo upload with photo picker
  - Header image upload
  - Opening hours editor
  - Validation

- [ ] `ListingEditorScreen.kt` (3-step wizard)
  - Step 1: Type Selection (PRODUCT/SERVICE/EVENT)
  - Step 2: Basic Info (title, description, photos, category)
  - Step 3: Type-specific details
  - Photo picker with multi-select

- [ ] `FeaturedCampaignsScreen.kt`
  - List user's listings for promotion
  - Active campaigns display
  - Stripe checkout integration

- [ ] `OwnerCitySlotsScreen.kt`
  - Available slots by city/type
  - Purchase flow with Stripe
  - Active subscriptions

### 4.4 Admin Mode Screens 🚧 PENDING (6 screens)

> **Note:** ViewModels exist, UI screens missing

- [ ] `AdminDashboardScreen.kt` — Full dashboard (placeholder exists)
- [ ] `ModerationListingsScreen.kt` — Review pending listings
- [ ] `ModerationBusinessesScreen.kt` — Review pending businesses
- [ ] `CitiesManagementScreen.kt` — CRUD for cities
- [ ] `UsersManagementScreen.kt` — User management
- [ ] `AuditLogsScreen.kt` — Audit log viewer

### 4.5 Navigation & Tab Bar 🚧 PENDING

**Basic Navigation (done):**
- [x] `Screen.kt` — Routes with Kotlin Serialization
- [x] `RixyNavGraph.kt` — Basic NavHost
- [x] Auth → CitySelector → CityHome flow

**Tab Navigation (pending):**
- [ ] `UserTabBarView.kt` — 5-tab navigation:
  - Home → CityHome
  - Search → BrowseListings
  - Favorites → FavoritesScreen
  - Orders → OrdersScreen (placeholder)
  - Profile → Settings
- [ ] Per-tab NavigationStack
- [ ] Tab state preservation

**Favorites System (pending):**
- [ ] `FavoritesViewModel.kt`
- [ ] `FavoritesScreen.kt` with staggered grid

### 4.6 Common Screens ✅ COMPLETE

- [x] `LoginScreen.kt`
- [x] `RegisterScreen.kt`
- [x] `SettingsScreen.kt`

### 4.7 Deep Links & Payments 🚧 PENDING

- [ ] Deep link handling for `rixy://payment/success`
- [ ] Deep link handling for `rixy://payment/cancel`
- [ ] AndroidManifest.xml intent filters
- [ ] Payment confirmation polling

### 4.8 Resources 🚧 PENDING

- [ ] Extract strings to `res/values/strings.xml`
- [ ] App icon and splash screen

### 4.9 Build Verification ✅

- [x] `./gradlew :app:compileDebugKotlin` passes

---

## Phase 4 Completion Checklist

### Priority 1: Owner Editor Screens (REQUIRED for iOS parity)
- [ ] BusinessEditorScreen.kt
- [ ] ListingEditorScreen.kt
- [ ] FeaturedCampaignsScreen.kt
- [ ] OwnerCitySlotsScreen.kt

### Priority 2: Tab Navigation (REQUIRED for iOS parity)
- [ ] UserTabBarView.kt (5 tabs)
- [ ] FavoritesViewModel.kt
- [ ] FavoritesScreen.kt

### Priority 3: Admin Screens (REQUIRED for iOS parity)
- [ ] AdminDashboardScreen.kt (complete implementation)
- [ ] ModerationListingsScreen.kt
- [ ] ModerationBusinessesScreen.kt
- [ ] CitiesManagementScreen.kt
- [ ] UsersManagementScreen.kt
- [ ] AuditLogsScreen.kt

### Priority 4: Payments & Deep Links (REQUIRED for iOS parity)
- [ ] Deep link handling
- [ ] Payment confirmation flow

### Priority 5: Polish
- [ ] String resources extraction
- [ ] App icon

---

## iOS vs Android Parity Summary

| Component | iOS Status | Android Status | Gap |
|-----------|------------|----------------|-----|
| Design System | 100% | 100% | ✅ |
| User Core Screens | 100% | 100% | ✅ |
| Owner Dashboard | 100% | 100% | ✅ |
| Owner Editor Screens | 100% | 0% | 🔴 4 screens missing |
| Admin Dashboard | 100% | 10% | 🔴 5 screens missing |
| Tab Navigation | 100% | 0% | 🔴 5 tabs + Favorites missing |
| Deep Links | 100% | 0% | 🔴 Not implemented |
| **TOTAL PARITY** | **100%** | **~65%** | **35% remaining** |

---

## Next Steps

**To complete Phase 4 (100% iOS parity):**

1. **Implement Owner Editor Screens** (4 screens)
2. **Implement Tab Navigation + Favorites** (5 tabs)
3. **Implement Admin Screens** (6 screens)
4. **Implement Deep Links & Payments**

**Estimated time to complete Phase 4:** ~3-4 weeks

---

## Technical Notes

### Payment Flow (Stripe)
1. ViewModel calls checkout use case → backend creates Stripe Checkout Session
2. Open session URL in Custom Tabs (Chrome)
3. Deep link callback: `rixy://payment/success?session_id=xxx`
4. Poll backend every 2s (max 10 attempts) until subscription ACTIVE

### Image Upload Flow
1. Request presigned URL from `/owner/uploads/presign`
2. PUT image data directly to presigned URL (S3/R2)
3. Use returned URL as `photoUrls` in listing/business

---
