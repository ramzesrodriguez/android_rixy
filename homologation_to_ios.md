# Android to iOS Homologation Technical Plan

Date: 2026-02-25  
Android app: `com.externalpods.rixy`  
iOS canonical baseline: `ios_reference/KeyCity` + `ios_reference/docs/*`

## 1) Objective

Guarantee strict parity with iOS for:
- Flow behavior
- Screen interaction model
- API integration contracts
- Navigation and role access
- Visual hierarchy and component behavior

This document is the implementation knowledge base to prevent UX rework and preserve what has already been validated in iOS.

---

## 2) Canonical References (Do Not Drift)

Use these as source of truth:
- `ios_reference/docs/02-user-mode-flows.md`
- `ios_reference/docs/03-owner-mode-flows.md`
- `ios_reference/docs/04-admin-mode-flows.md`
- `ios_reference/docs/05-business-rules-and-api-contracts.md`
- `ios_reference/docs/06-ios-android-homologation-matrix.md`
- `ios_reference/docs/08-screen-and-ui-catalog.md`

Primary iOS implementation files:
- User tab shell: `ios_reference/KeyCity/Presentation/Common/TabBar/UserTabBarView.swift`
- Settings/Profile: `ios_reference/KeyCity/Presentation/Common/Settings/SettingsView.swift`
- Login/Register: `ios_reference/KeyCity/Presentation/Auth/LoginView.swift`
- City home: `ios_reference/KeyCity/Presentation/User/CityHome/CityHomeView.swift`
- Owner dashboard: `ios_reference/KeyCity/Presentation/Owner/Dashboard/OwnerDashboardView.swift`
- Admin dashboard: `ios_reference/KeyCity/Presentation/Admin/Dashboard/AdminDashboardView.swift`
- Design tokens: `ios_reference/KeyCity/Presentation/Design/DSColors.swift`, `DSTokens.swift`

---

## 3) Global Product Rules

- iOS behavior is canonical; Android adapts to platform conventions only if UX logic remains equivalent.
- No placeholder screens in production user paths.
- No dead actions in critical flows.
- Role access must be enforced before mode/dashboard navigation.
- Orders tab must be controlled by feature flag parity.
- Critical flows must define success, error, and refresh behavior explicitly.

---

## 4) API Contract Inventory (By Domain)

## 4.1 Public/User APIs

### Cities and Home
1. `GET /api/v1/cities?activeOnly=true`
- Auth: No
- Purpose: city selector source
- Expected response: `ApiResponse<List<City>>`
- Required fields: `id`, `name`, `slug`, `isActive`

2. `GET /api/v1/{citySlug}/home`
- Auth: No
- Purpose: base home payload (city, featured, feed)
- Expected response: `CityHome` (non-wrapper contract)
- Required fields: `city`, `featured`, `feed`

3. `GET /api/v1/{citySlug}/home/sections`
- Auth: No
- Purpose: dynamic home sections list
- Expected response: `ApiResponse<List<CitySection>>`
- Required fields: `id`, `key`, `title`, `type`, `order`, `configJson`

4. `GET /api/v1/{citySlug}/home/sections/{sectionKey}/items?limit={n}`
- Auth: No
- Purpose: section items by `key`
- Expected response: `CitySectionItemsResponse`
- Required fields: `section`, `items[]`

5. `GET /api/v1/{citySlug}/slots`
- Auth: No
- Purpose: hero/carousel slot content
- Expected response: `ApiResponse<List<PublicCitySlot>>`
- Required fields: `slotType`, `slotIndex`, `listing`

### Listings and Businesses
6. `GET /api/v1/{citySlug}/listings?type&category&search&cursor`
- Auth: No
- Purpose: browse/search listings
- Expected response: `PaginatedResponse<Listing>`
- Required fields: `data[]`, `nextCursor`

7. `GET /api/v1/{citySlug}/listings/{listingId}`
- Auth: No
- Purpose: listing detail screen
- Expected response: `ApiResponse<Listing>`

8. `GET /api/v1/{citySlug}/businesses?search&cursor`
- Auth: No
- Purpose: businesses list
- Expected response: `PaginatedResponse<Business>`

9. `GET /api/v1/{citySlug}/businesses/{businessId}`
- Auth: No
- Purpose: business profile header/details
- Expected response: `ApiResponse<Business>`

10. `GET /api/v1/{citySlug}/businesses/{businessId}/listings?cursor`
- Auth: No
- Purpose: business listings feed
- Expected response: `PaginatedResponse<Listing>`

### Analytics
11. `POST /api/v1/{citySlug}/analytics/view`
- Auth: No
- Purpose: track listing/business views
- Body: `{ entityType, entityId }`

12. `POST /api/v1/slots/{assignmentId}/view` and `POST /api/v1/slots/{assignmentId}/click`
- Auth: No
- Purpose: slot analytics

## 4.2 Auth + Favorites APIs

1. Supabase auth (`signIn`, `signUp`, `signOut`, token retrieval)
- Purpose: authentication/session

2. `GET /api/v1/owner/me`
- Auth: Yes
- Purpose: user profile/role after auth

3. `GET /api/v1/owner/favorites/ids`
- Auth: Yes
- Purpose: favorite id set

4. `GET /api/v1/owner/favorites`
- Auth: Yes
- Purpose: full favorites list

5. `POST /api/v1/owner/favorites`
- Auth: Yes
- Body: `{ listingId }`
- Purpose: add favorite

6. `DELETE /api/v1/owner/favorites/{listingId}`
- Auth: Yes
- Purpose: remove favorite

Anonymous parity rule:
- If unauthorized, UI must remain functional with local favorites fallback.

## 4.3 Owner APIs

### Profile/Business/Listings
1. `GET /api/v1/owner/me`
2. `GET /api/v1/owner/analytics/overview?days=30`
3. `GET /api/v1/owner/business`
4. `POST /api/v1/owner/business`
5. `PUT /api/v1/owner/business`
6. `GET /api/v1/owner/listings`
7. `POST /api/v1/owner/listings`
8. `GET /api/v1/owner/listings/{listingId}`
9. `PUT /api/v1/owner/listings/{listingId}`
10. `DELETE /api/v1/owner/listings/{listingId}`

### Uploads
11. `POST /api/v1/owner/uploads/presign`
- Body: `{ filename, contentType }`
- Response data: `{ presignedUrl, publicUrl, key }`
- Follow-up: direct `PUT` file bytes to `presignedUrl`
- Persist in DB/UI: `publicUrl`

### Featured payments
12. `GET /api/v1/owner/featured`
13. `POST /api/v1/owner/featured/checkout`
14. `POST /api/v1/owner/featured/{listingId}/retry`
15. `POST /api/v1/owner/featured/{listingId}/cancel`
16. `POST /api/v1/owner/featured/{listingId}/renew`
17. `POST /api/v1/owner/featured/{listingId}/confirm`

### City slots
18. `GET /api/v1/owner/city-slots`
19. `GET /api/v1/owner/city-slots/availability?cityId={id}`
20. `GET /api/v1/owner/city-slots/subscriptions`
21. `GET /api/v1/owner/city-slots/subscriptions/history`
22. `POST /api/v1/owner/city-slots/checkout`
23. `POST /api/v1/owner/city-slots/subscriptions/{id}/retry`
24. `POST /api/v1/owner/city-slots/subscriptions/{id}/renew`
25. `POST /api/v1/owner/city-slots/{id}/confirm`
26. `POST /api/v1/owner/city-slots/{id}/cancel`

Payment parity rule:
- After callback/deep-link success, call confirm endpoint explicitly first.
- If still pending, use polling fallback with bounded timeout.

## 4.4 Admin APIs

1. `GET /api/v1/admin/cities`
2. `POST /api/v1/admin/cities`
3. `PUT /api/v1/admin/cities/{cityId}`
4. `GET /api/v1/admin/city-sections?cityId`
5. `POST /api/v1/admin/city-sections`
6. `PUT /api/v1/admin/city-sections/{sectionId}`
7. `DELETE /api/v1/admin/city-sections/{sectionId}`
8. `GET /api/v1/admin/moderation/listings`
9. `POST /api/v1/admin/moderation/listings/{listingId}/action`
10. `GET /api/v1/admin/moderation/listings/pending/count`
11. `GET /api/v1/admin/moderation/listings/stats`
12. `GET /api/v1/admin/moderation/businesses`
13. `POST /api/v1/admin/moderation/businesses/{businessId}/action`
14. `GET /api/v1/admin/moderation/businesses/pending/count`
15. `GET /api/v1/admin/users`
16. `PUT /api/v1/admin/users/{userId}/role`
17. `GET /api/v1/admin/featured`
18. `GET /api/v1/admin/pricing`
19. `PUT /api/v1/admin/pricing`
20. `GET /api/v1/admin/payments`
21. `GET /api/v1/admin/city-slots/subscriptions`
22. `POST /api/v1/admin/city-slots/{subscriptionId}/pause`
23. `POST /api/v1/admin/city-slots/{subscriptionId}/resume`
24. `POST /api/v1/admin/city-slots/{subscriptionId}/cancel`
25. `GET /api/v1/admin/city-slots/assignments`
26. `GET /api/v1/admin/audit`

Critical Android gap to close:
- `AdminRepository.suspendUser()` currently not implemented.

---

## 5) Screen Confirmation Framework (Use for Every Screen)

For each screen, confirm the following dimensions:
- Entry: where user comes from (routes/tabs/actions)
- Data source: API(s) and view model state inputs
- What user must see: required UI blocks
- What screen does: supported interactions
- What screen must NOT do: forbidden behavior
- Refresh behavior: initial load, pull-to-refresh, retry, resume
- Success expectations: expected state transitions and messages
- Error expectations: network/auth/empty handling
- iOS equivalence: canonical iOS file(s)

Use this as QA checklist and sign-off artifact.

---

## 6) Screen-by-Screen Confirmation Specs

## 6.1 USER SCREENS

### 6.1.1 City Selector
- Entry:
- App start with no selected city.
- From Home/Search/Profile city change action.
- Data source:
- `GET /cities?activeOnly=true`.
- Local search filter.
- Must show:
- Search field, city list/grid, loading, error, empty.
- Does:
- Persists selected city and routes to Home/Search context.
- Must NOT:
- Block app when API fails (provide retry/fallback path).
- Refresh:
- Initial `task`, manual refresh, retry.
- Success:
- City persisted and downstream Home uses correct slug.
- Error:
- Full-screen recoverable error + retry.
- iOS equivalence:
- `CitySelectorView.swift`.

### 6.1.2 City Home
- Entry:
- Home tab when city is selected.
- Data source:
- `/home`, `/home/sections`, `/home/sections/{key}/items`, `/slots`.
- Must show:
- City header + change action.
- Hero/featured area.
- Category grid.
- Slot content (hero + carousel).
- Dynamic sections.
- Recent feed.
- Does:
- Navigate to listing detail, business profile, browse/section routes.
- Must NOT:
- Render blank sections due to wrong section key mapping.
- Refresh:
- Initial load + pull-to-refresh + retry.
- Success:
- Home renders content from dynamic sections and slots.
- Error:
- Recoverable error state; no crash.
- iOS equivalence:
- `CityHomeView.swift`, `SectionListingsView.swift`.

### 6.1.3 Browse/Search
- Entry:
- Search tab or category route from Home.
- Data source:
- `/listings` with type/category/search/cursor.
- favorites ids from owner endpoints (fallback local if unauthorized).
- Must show:
- Search bar, filter chips, paginated grid, loading-more indicator.
- Does:
- Pagination, filter updates, favorite toggle from cards.
- Must NOT:
- Freeze listings if favorites endpoint is unauthorized.
- Refresh:
- Initial load, filter change, explicit refresh.
- Success:
- Smooth pagination and stable item keys.
- Error:
- Error view only for blocking failures; graceful fallback otherwise.
- iOS equivalence:
- `BrowseListingsView.swift`.

### 6.1.4 Listing Detail
- Entry:
- From Home/Search/Favorites/Business profile.
- Data source:
- `/listings/{id}` + view tracking endpoint.
- Must show:
- Header media, back/share/favorite actions, badge/type, title, business info, type details, CTA/contact block.
- Does:
- Navigate to business profile.
- Execute contact actions.
- Persist favorite state (same source as list/favorites).
- Must NOT:
- Keep favorite local-only in detail.
- Use wrong contact field mapping.
- Refresh:
- Initial load; retry on failure.
- Success:
- Consistent detail for product/service/event.
- Error:
- Recoverable detail error screen.
- iOS equivalence:
- `ListingDetailView.swift`.

### 6.1.5 Business Profile
- Entry:
- From listing detail or business list.
- Data source:
- `/businesses/{id}` + `/businesses/{id}/listings`.
- Must show:
- Business hero/header info.
- Contact actions.
- Listings list with pagination/loading states.
- Does:
- Navigate to listing detail.
- Must NOT:
- Break if business listing page is empty.
- Refresh:
- Initial load + retry.
- Success:
- Contact actions and listings fully usable.
- Error:
- Recoverable error states.
- iOS equivalence:
- `BusinessProfileView.swift`.

### 6.1.6 Favorites
- Entry:
- Favorites tab.
- Data source:
- Remote favorites endpoints (if auth).
- Local favorites store (if anon/offline/unauthorized).
- Must show:
- Search, type filters, favorites grid/list.
- Empty state when none.
- Does:
- Remove/toggle favorite optimistically.
- Persist favorites across app restarts.
- Sync local to remote after login.
- Must NOT:
- Require login to browse favorites.
- Refresh:
- Initial load, pull-to-refresh, retry.
- Success:
- Favorites survive restart and appear immediately.
- Error:
- Unauthorized should degrade to local mode, not crash.
- iOS equivalence:
- `FavoritesView` in `UserTabBarView.swift`.

### 6.1.7 Profile/Settings/Auth
- Entry:
- Profile tab.
- Data source:
- Auth state + selected city + user profile/role.
- Must show (guest):
- Language, selected city, login CTA, app version.
- Must show (authenticated):
- Account blocks, role/mode controls, sign out.
- Does:
- Open real login/register flow.
- Sign-out fully clears auth state.
- Mode switch only if role allows.
- Must NOT:
- Route to TODO screen.
- Show owner/admin path to unauthorized users.
- Refresh:
- React to auth and selected-city changes.
- Success:
- Correct guest/auth UI and transitions.
- Error:
- Auth errors shown with actionable retry.
- iOS equivalence:
- `SettingsView.swift`, `LoginView.swift`.

### 6.1.8 Orders
- Entry:
- Orders tab (if enabled).
- Must show:
- iOS-equivalent state according to feature flag.
- Must NOT:
- Diverge from iOS visibility decision.
- iOS equivalence:
- `OrdersView` in `UserTabBarView.swift`.

## 6.2 OWNER SCREENS

### 6.2.1 Owner Dashboard
- Entry:
- From role-approved mode switch.
- Data source:
- `owner/me`, `owner/business`, `owner/listings`, `owner/analytics/overview`.
- Must show:
- Header, business card/create CTA, KPIs, quick actions, recent listings, analytics preview.
- Does:
- Navigate to business editor, listing editor, featured, city slots.
- Must NOT:
- Be placeholder in production.
- Refresh:
- initial + pull-to-refresh + retry.
- Success:
- Owner can start all core owner tasks from dashboard.
- Error:
- recoverable error state with retry.
- iOS equivalence:
- `OwnerDashboardView.swift`.

### 6.2.2 Business Editor
- Entry:
- Owner dashboard action.
- Data source:
- `owner/business`, `createBusiness`, `updateBusiness`, presign upload.
- Must show:
- Full form fields + media upload + validation states.
- Does:
- Create/update business with valid city id.
- Must NOT:
- Submit create with empty city id.
- Refresh:
- load existing business if present.
- Success:
- Persisted business and returned state reflected.
- Error:
- validation and API errors visible and recoverable.
- iOS equivalence:
- `BusinessEditorView.swift`.

### 6.2.3 Listing Editor
- Entry:
- Owner dashboard quick action.
- Data source:
- owner listing CRUD + presign upload.
- Must show:
- 3-step wizard, progress indicator, step validations, type-specific forms.
- Does:
- create/update listing and persist media URLs.
- Must NOT:
- allow invalid step progress.
- Refresh:
- preload data in edit mode.
- Success:
- listing saved and reflected in owner listings.
- Error:
- field-level and API error handling.
- iOS equivalence:
- `ListingEditorView.swift`.

### 6.2.4 Featured Campaigns
- Entry:
- Owner dashboard.
- Data source:
- owner featured endpoints + payment callback handling.
- Must show:
- active/pending/history state groups.
- per-item action buttons by state.
- Does:
- checkout/retry/cancel/renew.
- on callback success: explicit confirm then polling fallback.
- Must NOT:
- stop at callback without confirm.
- Refresh:
- after any payment state transition.
- Success:
- placement reaches expected state and UI updates.
- Error:
- timeout and retry guidance.
- iOS equivalence:
- `FeaturedCampaignsView.swift`.

### 6.2.5 Owner City Slots
- Entry:
- Owner dashboard.
- Data source:
- slot availability/subscriptions/history + checkout endpoints.
- Must show:
- available slots, active slots, history sections.
- Does:
- select listing, buy slot, retry/renew/cancel.
- callback success -> confirm + polling fallback.
- Must NOT:
- submit slot purchase with empty listingId.
- Refresh:
- on city change and after actions.
- Success:
- subscription state transitions correctly.
- Error:
- recoverable per-action errors.
- iOS equivalence:
- `OwnerCitySlotsView.swift`.

## 6.3 ADMIN SCREENS

### 6.3.1 Admin Dashboard
- Entry:
- role-approved mode switch.
- Data source:
- pending counts + users + payments/revenue.
- Must show:
- stats cards and module navigation groups.
- Does:
- navigate to moderation/cities/sections/users/pricing/payments/featured/slots/audit.
- Must NOT:
- remain placeholder in production.
- Refresh:
- initial + manual refresh.
- Success:
- admin can access all modules from dashboard.
- Error:
- non-blocking error banner/retry.
- iOS equivalence:
- `AdminDashboardView.swift`.

### 6.3.2 Moderation Listings/Businesses
- Entry:
- from admin dashboard.
- Data source:
- moderation list + moderate action endpoints.
- Must show:
- pending/all filters, list, detail context, approve/reject with reason.
- Does:
- refresh list after moderation action.
- Must NOT:
- mutate status without user confirmation.
- Refresh:
- initial + retry + after action.
- Success:
- moderated items leave pending queue.
- Error:
- action errors shown inline/dialog.
- iOS equivalence:
- `ModerationListingsView.swift`, `ModerationBusinessesView.swift`.

### 6.3.3 Cities / Users / Audit / Other Admin Modules
- Entry:
- from admin dashboard module links.
- Data source:
- corresponding admin endpoints.
- Must show:
- list + filters + action controls per module.
- Does:
- create/update/toggle cities.
- role updates and suspension.
- audit logs with filters/details.
- pricing/payments/featured/slots admin parity.
- Must NOT:
- call unimplemented methods (`suspendUser`).
- Refresh:
- on module entry and after mutating actions.
- Success:
- persisted backend mutations reflected in lists.
- Error:
- clear recoverable error state.
- iOS equivalence:
- respective admin presentation files in iOS catalog.

---

## 7) Phase Implementation Plan with Structured Checklists

## Phase 0 - Runtime Architecture Hardening
- [ ] Define active navigation source of truth and deprecate placeholder host(s).
- [ ] Remove reachable placeholder paths.
- [ ] Add feature flags and default parity values.
- [ ] Add flow telemetry for key screen transitions.

Exit criteria:
- [ ] No placeholder screens reachable in user-critical paths.

## Phase 1 - Profile/Auth/Language Parity
- [x] Wire Profile -> Login -> Register real routes.
- [x] Wire sign-out in authenticated profile.
- [x] Implement language selector and runtime locale apply.
- [x] Add role guard before Owner/Admin mode switch.
- [x] Remove dead actions or hide unavailable items.
- [x] Refresh Profile state after Login/Register via shared app state.

Exit criteria:
- [ ] Guest/auth profile flow parity approved against iOS.
- [ ] Validate guest profile blocks against iOS (`Idioma`, `Ciudad seleccionada`, `Cuenta`, `Acerca de`).
- [ ] Validate login navigation from Profile and back navigation behavior.
- [ ] Validate register flow returns to Profile in authenticated state.
- [ ] Validate sign-out returns to guest profile state without stale user data.
- [ ] Validate language change persists after app restart.

## Phase 2 - Owner Flow Completion
- [ ] Replace owner dashboard placeholder with full feature shell.
- [ ] Fix business create city binding.
- [ ] Fix city slots purchase listing selection.
- [ ] Complete featured/slots payment callback confirm + polling.
- [ ] Validate owner editors against iOS interaction sequence.

Exit criteria:
- [ ] Owner E2E flow passes without dead ends.

## Phase 3 - Admin Flow Completion
- [ ] Replace admin dashboard placeholder with module navigation shell.
- [ ] Implement missing admin actions (`suspendUser`).
- [ ] Connect all module screens and route guards.
- [ ] Validate moderation and admin operations parity.

Exit criteria:
- [ ] Admin can execute core operations end-to-end.

## Phase 4 - UI Interaction and Token Parity
- [ ] Finalize canonical token map with product.
- [ ] Align headers/insets/spacing/typography by screen.
- [ ] Align card states, badges, filters, empty/error states.
- [ ] Complete side-by-side iOS vs Android screenshot review.

Exit criteria:
- [ ] Product sign-off on visual and interaction parity.

## Phase 5 - QA Hardening and Release Gate
- [ ] E2E matrix: Guest/User/Owner/Admin.
- [ ] Error matrix: unauthorized/forbidden/not found/server/offline.
- [ ] Persistence matrix: favorites/city/language/session.
- [ ] Performance matrix: startup/scroll/image-heavy screens.

Exit criteria:
- [ ] Release checklist signed, no critical regressions open.

---

## 8) Master Sign-off Checklist

- [ ] API contracts match iOS behavior and backend expectations.
- [ ] All critical screens have completed confirmation specs.
- [ ] All critical flows have success/error/refresh behavior validated.
- [ ] All role-gated routes are enforced.
- [ ] No TODO placeholder or dead interaction remains.
- [ ] Product/design approved parity across User, Owner, Admin domains.

---

## 9) Definition of Done (Per Screen)

A screen is homologated only if all are true:
- [ ] Entry points are fully wired.
- [ ] APIs and view-model state map correctly to UI.
- [ ] Expected UI blocks render with iOS-equivalent structure.
- [ ] Disallowed behavior is prevented.
- [ ] Refresh semantics match iOS.
- [ ] Success and error outcomes are explicitly implemented.
- [ ] QA checklist passed and signed.
