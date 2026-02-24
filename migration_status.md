# Rixy Android — iOS Migration Status & Architectural Plan

> **Source:** iOS Reference App "KeyCity" (`/ios_reference/`)
> **Target:** Android App "Rixy" (`com.externalpods.rixy`)
> **Architecture Guide:** `docs/ANDROID_APP_ARCHITECTURE.md`
> **Last Updated:** 2026-02-23

---

## Overall Progress

| Phase | Name | Status | Approved |
|-------|------|--------|----------|
| 1 | Architectural Analysis & Discovery | ✅ Complete | ✅ |
| 2 | Data & Domain Layers (The Core) | ✅ Complete | ✅ |
| 3 | Presentation Layer (ViewModels) | ⬜ Pending | ⬜ |
| 4 | UI Layer (Jetpack Compose) | ⬜ Pending | ⬜ |

---

## PHASE 1: Architectural Analysis & Discovery ✅

### 1.1 iOS App Summary

- **App Name:** KeyCity (iOS) → Rixy (Android)
- **Total:** 92 Swift files, 8000+ LOC, 7 markdown docs
- **Architecture:** MVVM + Clean Architecture, SwiftUI, @Observable, async/await
- **3 Modes:** User (consumer), Owner (seller), Admin (platform operator)
- **Backend:** Custom REST API + Supabase Auth + Stripe Payments
- **Features:** 60+ API endpoints, 40+ data models, 30+ enums, 12+ ViewModels, 20+ screens

### 1.2 Module Decision

**Single `:app` module with strict package-level separation.**

Rationale: For ~10-12K LOC, multi-module Gradle adds build overhead without team-parallelism benefits. Packages are designed for easy future extraction into modules.

### 1.3 Android Package Structure

```
com.externalpods.rixy/
├── RixyApplication.kt                    # Application class, Koin startup
├── MainActivity.kt                       # Single Activity, NavHost host
│
├── core/
│   ├── model/                            # Pure Kotlin data classes + enums
│   │   ├── Enums.kt                      # All enums (AppMode, ListingType, etc.)
│   │   ├── City.kt                       # City, CitySection, CityHome, CitySummary
│   │   ├── Owner.kt                      # Owner (user account)
│   │   ├── Business.kt                   # Business, BusinessSection, BusinessSummary
│   │   ├── Listing.kt                    # Listing, ProductDetails, ServiceDetails, EventDetails
│   │   ├── Featured.kt                   # FeaturedPlacement, ListingSummary
│   │   ├── CitySlot.kt                   # CitySlotSubscription, CitySlotAssignment
│   │   ├── Analytics.kt                  # OwnerAnalyticsOverview
│   │   ├── AuditLog.kt                   # AuditLog
│   │   └── ApiResponse.kt               # ApiResponse<T>, PaginatedResponse
│   │
│   ├── network/
│   │   ├── ApiConfig.kt                  # Base URLs, Supabase config
│   │   ├── AuthInterceptor.kt            # OkHttp interceptor (Bearer token)
│   │   ├── PublicApiService.kt           # Retrofit interface
│   │   ├── OwnerApiService.kt            # Retrofit interface
│   │   ├── AdminApiService.kt            # Retrofit interface
│   │   ├── dto/                          # Request DTOs
│   │   │   ├── BusinessRequests.kt
│   │   │   ├── ListingRequests.kt
│   │   │   ├── AdminRequests.kt
│   │   │   └── CommonRequests.kt
│   │   └── NetworkModule.kt              # Koin module
│   │
│   ├── common/
│   │   ├── UiState.kt                    # sealed class: Loading / Success / Error
│   │   ├── CurrencyFormatter.kt
│   │   ├── DateUtils.kt
│   │   └── Extensions.kt
│   │
│   └── designsystem/
│       ├── theme/
│       │   ├── Color.kt                  # RixyColors
│       │   ├── Type.kt                   # RixyTypography (Inter font)
│       │   ├── Shape.kt                  # RixyShapes
│       │   ├── Spacing.kt               # Spacing tokens
│       │   └── Theme.kt                 # RixyTheme composable
│       └── components/
│           ├── ListingCard.kt
│           ├── CityCard.kt
│           ├── StatusBadge.kt
│           ├── SectionHeader.kt
│           ├── SearchBar.kt
│           ├── SkeletonView.kt
│           ├── EmptyStateView.kt
│           ├── RixyButton.kt
│           ├── RixyTextField.kt
│           └── RixyCard.kt
│
├── data/
│   ├── repository/
│   │   ├── CityRepository.kt            # Interface + Impl
│   │   ├── ListingRepository.kt
│   │   ├── BusinessRepository.kt
│   │   ├── OwnerRepository.kt
│   │   └── AdminRepository.kt
│   ├── local/
│   │   ├── DataStoreManager.kt           # Preferences DataStore
│   │   └── TokenManager.kt              # EncryptedSharedPreferences
│   └── DataModule.kt                     # Koin module
│
├── domain/
│   ├── usecase/
│   │   ├── city/                         # GetCitiesUseCase, GetCityHomeUseCase
│   │   ├── listing/                      # GetListingsUseCase, CreateListingUseCase, etc.
│   │   ├── business/                     # GetBusinessUseCase, CreateBusinessUseCase
│   │   ├── owner/                        # GetOwnerProfileUseCase, GetAnalyticsUseCase
│   │   └── admin/                        # ModerateListingUseCase, ManageCityUseCase
│   └── DomainModule.kt                   # Koin module
│
├── service/
│   ├── AuthService.kt                    # Supabase auth + token management
│   ├── PaymentService.kt                # Stripe PaymentSheet + checkout
│   ├── ImageUploadService.kt            # Presigned URL → S3/R2 upload
│   ├── AnalyticsService.kt              # Fire-and-forget tracking
│   └── ServiceModule.kt                 # Koin module
│
├── feature/
│   ├── auth/
│   │   ├── LoginScreen.kt + LoginViewModel.kt
│   │   └── RegisterScreen.kt + RegisterViewModel.kt
│   ├── user/
│   │   ├── cityselector/                 # CitySelectorScreen + VM
│   │   ├── cityhome/                     # CityHomeScreen + VM
│   │   ├── listingdetail/               # ListingDetailScreen + VM
│   │   ├── businessprofile/             # BusinessProfileScreen + VM
│   │   └── browse/                       # BrowseListingsScreen + VM
│   ├── owner/
│   │   ├── dashboard/                    # OwnerDashboardScreen + VM
│   │   ├── business/                     # BusinessEditorScreen + VM
│   │   ├── listings/                     # ListingEditorScreen + VM
│   │   ├── featured/                     # FeaturedCampaignsScreen + VM
│   │   └── cityslots/                    # OwnerCitySlotsScreen + VM
│   ├── admin/
│   │   ├── dashboard/                    # AdminDashboardScreen + VM
│   │   ├── cities/                       # CitiesManagementScreen + VM
│   │   ├── moderation/                   # ModerationListings + ModerationBusinesses + VM
│   │   ├── users/                        # UsersManagementScreen + VM
│   │   └── audit/                        # AuditLogsScreen + VM
│   └── settings/
│       └── SettingsScreen.kt + SettingsViewModel.kt
│
└── navigation/
    ├── Screen.kt                         # @Serializable sealed interface routes
    ├── RixyNavGraph.kt                   # NavHost (User/Owner/Admin sub-graphs)
    └── AppState.kt                       # Global state (mode, city, auth) via StateFlow
```

### 1.4 Dependency Graph

```
                     ┌──────────────┐
                     │  App Root    │
                     │ (MainActivity│
                     │  RixyApp)    │
                     └──────┬───────┘
                            │
                 ┌──────────┼──────────┐
                 │          │          │
           ┌─────▼─────┐   │   ┌──────▼──────┐
           │ navigation │   │   │  feature/*  │
           └─────┬──────┘   │   └──────┬──────┘
                 │          │          │
                 │     ┌────▼────┐     │
                 │     │ service │     │
                 │     └────┬────┘     │
                 │          │          │
           ┌─────▼──────────▼──────────▼──────┐
           │            domain/                │
           └────────────┬──────────────────────┘
                        │
           ┌────────────▼──────────────────────┐
           │            data/                   │
           └────────────┬──────────────────────┘
                        │
      ┌─────────┬───────┴───────┬──────────┐
 ┌────▼───┐ ┌──▼─────┐ ┌──────▼──┐ ┌─────▼─────┐
 │ core/  │ │ core/  │ │ core/   │ │ core/     │
 │ model  │ │network │ │ common  │ │designsys  │
 └────────┘ └────────┘ └─────────┘ └───────────┘
```

### 1.5 iOS → Android Class Mapping

#### Models & Enums
| iOS File | Android File | Package |
|---|---|---|
| `AppEnums.swift` (30+ enums) | `Enums.kt` | `core.model` |
| `CityModels.swift` | `City.kt` | `core.model` |
| `UserModels.swift` | `Owner.kt` | `core.model` |
| `ListingModels.swift` | `Listing.kt` | `core.model` |
| `BusinessSectionModels.swift` | `Business.kt` | `core.model` |
| `FeaturedModels.swift` | `Featured.kt` | `core.model` |
| `CitySlotModels.swift` | `CitySlot.kt` | `core.model` |
| `AnalyticsModels.swift` | `Analytics.kt` | `core.model` |

#### Data Layer
| iOS | Android | Package |
|---|---|---|
| `APIClient.swift` (URLSession) | Retrofit + OkHttp via `NetworkModule.kt` | `core.network` |
| `PublicAPI.swift` | `PublicApiService.kt` (Retrofit interface) | `core.network` |
| `OwnerAPI.swift` | `OwnerApiService.kt` | `core.network` |
| `AdminAPI.swift` | `AdminApiService.kt` | `core.network` |
| `CityRepository.swift` | `CityRepository.kt` (interface + impl) | `data.repository` |
| `ListingRepository.swift` | `ListingRepository.kt` | `data.repository` |
| `BusinessRepository.swift` | `BusinessRepository.kt` | `data.repository` |
| `OwnerRepository.swift` | `OwnerRepository.kt` | `data.repository` |
| `KeychainManager.swift` | `TokenManager.kt` (EncryptedSharedPrefs) | `data.local` |
| `UserDefaultsManager.swift` | `DataStoreManager.kt` (DataStore) | `data.local` |

#### ViewModels (iOS @Observable → Android ViewModel + StateFlow)
| iOS ViewModel | Android ViewModel | Feature Package |
|---|---|---|
| `CitySelectorViewModel` | `CitySelectorViewModel` | `feature.user.cityselector` |
| `CityHomeViewModel` | `CityHomeViewModel` | `feature.user.cityhome` |
| `ListingDetailViewModel` | `ListingDetailViewModel` | `feature.user.listingdetail` |
| `BusinessProfileViewModel` | `BusinessProfileViewModel` | `feature.user.businessprofile` |
| `BrowseListingsViewModel` | `BrowseListingsViewModel` | `feature.user.browse` |
| `OwnerDashboardViewModel` | `OwnerDashboardViewModel` | `feature.owner.dashboard` |
| `BusinessEditorViewModel` | `BusinessEditorViewModel` | `feature.owner.business` |
| `ListingEditorViewModel` | `ListingEditorViewModel` | `feature.owner.listings` |
| `FeaturedCampaignsViewModel` | `FeaturedCampaignsViewModel` | `feature.owner.featured` |
| `OwnerCitySlotsViewModel` | `OwnerCitySlotsViewModel` | `feature.owner.cityslots` |
| `AdminDashboardViewModel` | `AdminDashboardViewModel` | `feature.admin.dashboard` |

#### Services (iOS .shared → Koin `single {}`)
| iOS | Android | Koin Scope |
|---|---|---|
| `AuthService.shared` | `AuthService` | `single {}` in `ServiceModule` |
| `PaymentService.shared` | `PaymentService` | `single {}` in `ServiceModule` |
| `ImageUploadService.shared` | `ImageUploadService` | `single {}` in `ServiceModule` |
| `AnalyticsService.shared` | `AnalyticsService` | `single {}` in `ServiceModule` |

#### State Management
| iOS | Android |
|---|---|
| `AppState` (@Observable @MainActor) | `AppState` class with `StateFlow` fields |
| `@Published var currentMode` | `MutableStateFlow<AppMode>` |
| `@Published var selectedCity` | `MutableStateFlow<City?>` |
| `.environment(appState)` | `koinInject<AppState>()` |

### 1.6 Koin Module Organization

| Module | File | Contents | Scope |
|---|---|---|---|
| `networkModule` | `core/network/NetworkModule.kt` | Json, OkHttp, Retrofit, 3 API services | `single` |
| `dataModule` | `data/DataModule.kt` | DataStore, TokenManager, 5 Repositories | `single` |
| `domainModule` | `domain/DomainModule.kt` | All Use Cases | `factory` |
| `serviceModule` | `service/ServiceModule.kt` | Auth, Payment, Upload, Analytics, AppState | `single` |
| `featureModule` | `feature/FeatureModule.kt` | All ViewModels | `viewModel` |

### 1.7 Navigation Architecture

```
AppMode.USER  → UserNavHost()   → BottomBar (Home, Search, Favorites, Settings)
AppMode.OWNER → OwnerNavHost()  → Dashboard-centric NavigationStack
AppMode.ADMIN → AdminNavHost()  → Drawer + NavigationStack
```

Deep linking: `rixy://payment/success?session_id=xxx`, `rixy://payment/cancel`

### 1.8 Gradle Dependencies to Add

| Library | Purpose | iOS Equivalent |
|---|---|---|
| Koin Android + Compose | DI | Singleton pattern |
| Retrofit + OkHttp | HTTP client | URLSession/APIClient |
| kotlinx-serialization | JSON | Codable |
| Coil Compose | Image loading/caching | Kingfisher |
| Navigation Compose | Navigation | NavigationStack |
| DataStore Preferences | Preferences storage | UserDefaults |
| Supabase Kotlin Client | Authentication | Supabase Swift |
| Stripe Android SDK | Payments | Stripe iOS |
| Lifecycle ViewModel Compose | ViewModel in Compose | @Observable |

---

## PHASE 2: Data & Domain Layers (The Core)

> **Status:** ⬜ Pending Approval
> **Goal:** Build the entire foundation — models, networking, repositories, use cases, DI

### 2.1 Gradle Setup
- [ ] Add all dependency versions to `gradle/libs.versions.toml`
- [ ] Add `kotlin-serialization` plugin to root `build.gradle.kts`
- [ ] Apply serialization plugin in `app/build.gradle.kts`
- [ ] Add all library dependencies to `app/build.gradle.kts`
- [ ] Add INTERNET permission to `AndroidManifest.xml`
- [ ] Verify build: `./gradlew assembleDebug`

### 2.2 Core Models (`core/model/`)
- [ ] `Enums.kt` — All enums with `@SerialName` for JSON mapping (AppMode, OwnerRole, OwnerStatus, BusinessStatus, ListingType, ListingStatus, PriceType, StockStatus, Condition, PricingModel, ServiceAreaType, EventStatus, FeaturedPlacementStatus, CitySlotStatus, CitySlotType, PaymentStatus, CitySectionType, ModerationAction)
- [ ] `City.kt` — City, CitySection, CityHome, CitySummary, PublicCitySlot
- [ ] `Owner.kt` — Owner data class
- [ ] `Business.kt` — Business, BusinessSection, BusinessSummary
- [ ] `Listing.kt` — Listing, ProductDetails, ServiceDetails, EventDetails, ListingSummary, BusinessSummary, DeliveryOptions, ProductAttributes
- [ ] `Featured.kt` — FeaturedPlacement
- [ ] `CitySlot.kt` — CitySlotSubscription, CitySlotAssignment, CitySlotSubscriptionCountItem
- [ ] `Analytics.kt` — OwnerAnalyticsOverview and sub-models
- [ ] `AuditLog.kt` — AuditLog
- [ ] `ApiResponse.kt` — ApiResponse<T> wrapper, PaginatedResponse

### 2.3 Core Network (`core/network/`)
- [ ] `ApiConfig.kt` — Base URLs (dev: `http://10.0.2.2:3000/api/v1/`, prod: `https://api.rixy.app/api/v1/`), Supabase URL/key
- [ ] `AuthInterceptor.kt` — OkHttp interceptor that adds Bearer token from TokenManager
- [ ] `PublicApiService.kt` — Retrofit interface for all public endpoints (~15 endpoints)
- [ ] `OwnerApiService.kt` — Retrofit interface for all owner endpoints (~20 endpoints)
- [ ] `AdminApiService.kt` — Retrofit interface for all admin endpoints (~20 endpoints)
- [ ] `dto/BusinessRequests.kt` — CreateBusinessRequest, UpdateBusinessRequest
- [ ] `dto/ListingRequests.kt` — CreateListingRequest, UpdateListingRequest
- [ ] `dto/AdminRequests.kt` — ModerationActionRequest, CreateCityRequest, UpdateCityRequest, UpdateUserRoleRequest, CreateCitySectionRequest, UpdatePricingRequest
- [ ] `dto/CommonRequests.kt` — PresignRequest, CheckoutRequest, AnalyticsEventRequest
- [ ] `NetworkModule.kt` — Koin module: Json config, OkHttp with AuthInterceptor + logging, Retrofit, all 3 API services

### 2.4 Core Common (`core/common/`)
- [ ] `UiState.kt` — `sealed class UiState<out T> { Loading, Success(data), Error(message) }`
- [ ] `CurrencyFormatter.kt` — Format MXN amounts (from String prices)
- [ ] `DateUtils.kt` — ISO8601 parsing, relative time formatting
- [ ] `Extensions.kt` — Useful Kotlin/Compose extensions

### 2.5 Data Layer — Local Storage (`data/local/`)
- [ ] `TokenManager.kt` — Save/get/clear auth token using EncryptedSharedPreferences
- [ ] `DataStoreManager.kt` — Preferences DataStore for: selectedCityId, selectedCitySlug, selectedCityName, currentMode, isAuthenticated, currentUserId, currentUserEmail

### 2.6 Data Layer — Repositories (`data/repository/`)
- [ ] `CityRepository.kt` — Interface + Impl (getCities, getCityHome, getCityInfo, getPublicSlots)
- [ ] `ListingRepository.kt` — Interface + Impl (getListings, getListingDetail, getBusinessListings)
- [ ] `BusinessRepository.kt` — Interface + Impl (getBusinesses, getBusinessDetail, getReviews, createReview)
- [ ] `OwnerRepository.kt` — Interface + Impl (getProfile, getBusiness, createBusiness, updateBusiness, getListings, createListing, updateListing, deleteListing, getPresignedUrl, getFeatured, createFeaturedCheckout, getCitySlots, createCitySlotCheckout, getAnalytics, getFavoriteIds, addFavorite, removeFavorite)
- [ ] `AdminRepository.kt` — Interface + Impl (getCities, createCity, updateCity, getModerationListings, moderateListing, getModerationBusinesses, moderateBusiness, getUsers, updateUserRole, getFeatured, getCitySlotSubscriptions, pauseSlot, resumeSlot, getPricing, updatePricing, getPayments, getAuditLogs, getCitySections, createCitySection, updateCitySection, deleteCitySection)
- [ ] `DataModule.kt` — Koin module: DataStoreManager, TokenManager, all repository bindings

### 2.7 Domain Layer — Use Cases (`domain/usecase/`)
- [ ] `city/GetCitiesUseCase.kt`
- [ ] `city/GetCityHomeUseCase.kt`
- [ ] `listing/GetListingsUseCase.kt`
- [ ] `listing/GetListingDetailUseCase.kt`
- [ ] `listing/CreateListingUseCase.kt`
- [ ] `listing/UpdateListingUseCase.kt`
- [ ] `listing/DeleteListingUseCase.kt`
- [ ] `business/GetBusinessUseCase.kt`
- [ ] `business/GetBusinessListingsUseCase.kt`
- [ ] `business/CreateBusinessUseCase.kt`
- [ ] `business/UpdateBusinessUseCase.kt`
- [ ] `owner/GetOwnerProfileUseCase.kt`
- [ ] `owner/GetAnalyticsUseCase.kt`
- [ ] `admin/GetModerationListingsUseCase.kt`
- [ ] `admin/ModerateListingUseCase.kt`
- [ ] `admin/GetModerationBusinessesUseCase.kt`
- [ ] `admin/ModerateBusinessUseCase.kt`
- [ ] `admin/ManageCitiesUseCase.kt`
- [ ] `admin/ManageUsersUseCase.kt`
- [ ] `DomainModule.kt` — Koin module: all use cases as `factory {}`

### 2.8 Services (`service/`)
- [ ] `AuthService.kt` — Supabase sign in/up/out, token refresh, session check, loadUser
- [ ] `PaymentService.kt` — Stripe PaymentSheet + checkout URL flow + polling
- [ ] `ImageUploadService.kt` — Get presigned URL + PUT to S3/R2
- [ ] `AnalyticsService.kt` — Fire-and-forget view/click tracking
- [ ] `ServiceModule.kt` — Koin module: all services as `single {}`

### 2.9 App Wiring
- [ ] `RixyApplication.kt` — Application class, startKoin with all 5 modules
- [ ] Register `RixyApplication` in `AndroidManifest.xml`
- [ ] Verify build: `./gradlew assembleDebug` passes

---

## PHASE 3: Presentation Layer (ViewModels)

> **Status:** ⬜ Pending Approval
> **Goal:** Translate all iOS ViewModels to Android ViewModels with StateFlow (UDF pattern)

### 3.1 Global State
- [ ] `navigation/AppState.kt` — StateFlow-based class: currentMode, selectedCity, isAuthenticated, currentUser. Methods: switchMode(), selectCity(), signIn(), signOut(). Persists to DataStore.

### 3.2 User Mode ViewModels
- [ ] `feature/user/cityselector/CitySelectorViewModel.kt` — Load cities, search/filter, select city
- [ ] `feature/user/cityhome/CityHomeViewModel.kt` — Load city home (hero, featured, sections, feed, slots)
- [ ] `feature/user/listingdetail/ListingDetailViewModel.kt` — Load listing detail, track view, handle contact actions
- [ ] `feature/user/businessprofile/BusinessProfileViewModel.kt` — Load business profile + reviews + listings
- [ ] `feature/user/browse/BrowseListingsViewModel.kt` — Search, filter by type/category, cursor pagination

### 3.3 Owner Mode ViewModels
- [ ] `feature/owner/dashboard/OwnerDashboardViewModel.kt` — Load analytics overview, listing counts, quick actions
- [ ] `feature/owner/business/BusinessEditorViewModel.kt` — Create/edit business form, image upload, validation
- [ ] `feature/owner/listings/ListingEditorViewModel.kt` — 3-step wizard (Type → Basic → Details), type-specific fields, image upload
- [ ] `feature/owner/featured/FeaturedCampaignsViewModel.kt` — Load placements, initiate Stripe checkout
- [ ] `feature/owner/cityslots/OwnerCitySlotsViewModel.kt` — Load subscriptions, purchase slot, cancel

### 3.4 Admin Mode ViewModels
- [ ] `feature/admin/dashboard/AdminDashboardViewModel.kt` — Load pending counts, stats, revenue
- [ ] `feature/admin/cities/CitiesManagementViewModel.kt` — CRUD cities, toggle active/publishing/ads
- [ ] `feature/admin/moderation/ModerationViewModel.kt` — Load queues, approve/reject/suspend with reason
- [ ] `feature/admin/users/UsersManagementViewModel.kt` — Load users, change roles
- [ ] `feature/admin/audit/AuditLogsViewModel.kt` — Load audit logs with pagination

### 3.5 Auth & Settings ViewModels
- [ ] `feature/auth/LoginViewModel.kt` — Email/password sign in via AuthService
- [ ] `feature/auth/RegisterViewModel.kt` — Email/password sign up
- [ ] `feature/settings/SettingsViewModel.kt` — Mode switching, sign out, user info display

### 3.6 Feature Koin Module
- [ ] `feature/FeatureModule.kt` — All ViewModels registered with `viewModel { }`

### 3.7 Navigation
- [ ] `navigation/Screen.kt` — @Serializable sealed interface with all route definitions
- [ ] `navigation/RixyNavGraph.kt` — NavHost skeleton with all 3 mode sub-graphs
- [ ] Verify build: `./gradlew assembleDebug` passes

---

## PHASE 4: UI Layer (Jetpack Compose)

> **Status:** ⬜ Pending Approval
> **Goal:** Build all screens with Compose + Material3, design system, and full navigation

### 4.1 Design System Theme
- [ ] `core/designsystem/theme/Color.kt` — RixyColors: Primary (#F97316), Secondary (#6B7280), Tertiary (#3B82F6), type colors, semantic colors, light/dark schemes
- [ ] `core/designsystem/theme/Type.kt` — Full Material3 Typography with Inter font
- [ ] `core/designsystem/theme/Shape.kt` — RixyShapes: small (8dp), medium (12dp), large (16dp), extraLarge (24dp)
- [ ] `core/designsystem/theme/Spacing.kt` — xs (4dp), sm (8dp), md (16dp), lg (24dp), xl (32dp), xxl (48dp)
- [ ] `core/designsystem/theme/Theme.kt` — RixyTheme composable with dynamic color support

### 4.2 Reusable Components
- [ ] `ListingCard.kt` — 4:3 image, title, price, type badge, business name
- [ ] `CityCard.kt` — City image + name + counts overlay (min 160dp)
- [ ] `StatusBadge.kt` — Color-coded status chips (Published=green, Pending=yellow, etc.)
- [ ] `SectionHeader.kt` — Title + optional "See All" action
- [ ] `SearchBar.kt` — Outlined text field with search icon
- [ ] `SkeletonView.kt` — Shimmer loading placeholders
- [ ] `EmptyStateView.kt` — Icon + title + subtitle + optional CTA
- [ ] `RixyButton.kt` — Primary/secondary/outlined variants
- [ ] `RixyTextField.kt` — Styled OutlinedTextField
- [ ] `RixyCard.kt` — Base card with consistent elevation/radius

### 4.3 User Mode Screens
- [ ] `CitySelectorScreen.kt` — Hero + 2-column city grid + search
- [ ] `CityHomeScreen.kt` — Hero, featured listing, dynamic sections, feed (LazyColumn)
- [ ] `ListingDetailScreen.kt` — Image carousel (HorizontalPager), pricing, description, contact buttons (WhatsApp, phone, web)
- [ ] `BusinessProfileScreen.kt` — Header, info (hours, address, contact), listings grid, reviews
- [ ] `BrowseListingsScreen.kt` — Search bar, type filter chips, listing grid, cursor pagination
- [ ] `UserTabBar` — BottomNavigation: Home, Search, Favorites, Settings

### 4.4 Owner Mode Screens
- [ ] `OwnerDashboardScreen.kt` — Analytics cards, listing count, quick action buttons
- [ ] `BusinessEditorScreen.kt` — Form: name, description, logo upload, header upload, address, phone, hours, website
- [ ] `ListingEditorScreen.kt` — 3-step wizard: Type selection → Basic info (title, desc, images) → Type-specific details (Product/Service/Event fields)
- [ ] `FeaturedCampaignsScreen.kt` — Active/available placements list, purchase CTA → Stripe
- [ ] `OwnerCitySlotsScreen.kt` — Subscriptions list, slot types, purchase/cancel

### 4.5 Admin Mode Screens
- [ ] `AdminDashboardScreen.kt` — Stat cards (pending moderation, revenue, users), navigation to management screens
- [ ] `CitiesManagementScreen.kt` — City list + create/edit dialog
- [ ] `ModerationListingsScreen.kt` — Queue list, approve/reject/suspend with reason dialog
- [ ] `ModerationBusinessesScreen.kt` — Same pattern as listings moderation
- [ ] `UsersManagementScreen.kt` — User list, role change dialog
- [ ] `AuditLogsScreen.kt` — Scrollable audit log list with filters

### 4.6 Common Screens
- [ ] `LoginScreen.kt` — Email + password form, sign in/up toggle
- [ ] `RegisterScreen.kt` — Email + password + confirm form
- [ ] `SettingsScreen.kt` — User info, mode switcher, sign out, app version

### 4.7 Navigation Wiring
- [ ] Wire all screens into `RixyNavGraph.kt` with proper NavHost destinations
- [ ] Implement User BottomNavigation with per-tab backstacks
- [ ] Implement Owner navigation (dashboard-centric)
- [ ] Implement Admin navigation (drawer)
- [ ] Configure deep link handling in `MainActivity` for `rixy://` scheme
- [ ] Add intent filter in `AndroidManifest.xml` for deep links

### 4.8 Resources
- [ ] `res/values/strings.xml` — All user-facing strings (Spanish primary)
- [ ] `res/values/colors.xml` — Resource colors for XML fallback
- [ ] Update `res/values/themes.xml` to reference RixyTheme
- [ ] App icon / splash screen (if needed)

### 4.9 Final Verification
- [ ] `./gradlew assembleDebug` passes
- [ ] App launches and shows CitySelector
- [ ] Navigation between all 3 modes works
- [ ] All screens render with proper loading/success/error states
- [ ] Deep links for payment callbacks work
- [ ] Back button behavior correct for all navigation stacks

---

## Technical Notes

### Snake_case JSON Handling
iOS uses `keyDecodingStrategy = .convertFromSnakeCase`. Android: configure `kotlinx.serialization.Json` with `JsonNamingStrategy.SnakeCase` globally, or use `@SerialName("field_name")` per field.

### Price Handling
iOS stores prices as `String` (not Decimal) for precision. Android: use `String` in data classes, convert to formatted display via `CurrencyFormatter`.

### Authentication Flow
1. User signs in via Supabase → JWT token
2. Token stored in EncryptedSharedPreferences (`TokenManager`)
3. `AuthInterceptor` adds `Bearer {token}` to all authenticated requests
4. `AuthService` handles refresh and session validation

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

## Reference Files

| Purpose | Path |
|---|---|
| Architecture Guide | `docs/ANDROID_APP_ARCHITECTURE.md` |
| iOS Architecture | `ios_reference/IOS_APP_ARCHITECTURE.md` |
| iOS Maintenance Guide | `ios_reference/KeyCity/iOS_MAINTENANCE_GUIDE.md` |
| Design System Spec | `ios_reference/APP_DS.md` |
| Admin API Reference | `ios_reference/ADMIN_API_IOS_GUIDE.md` |
| Developer Standards | `ios_reference/DEVELOPER_MANIFEST.md` |
| Version Catalog | `gradle/libs.versions.toml` |
| App Build Config | `app/build.gradle.kts` |
