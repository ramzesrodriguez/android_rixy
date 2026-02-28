package com.externalpods.rixy.feature

import com.externalpods.rixy.feature.admin.audit.AuditLogsViewModel
import com.externalpods.rixy.feature.admin.cities.CitiesManagementViewModel
import com.externalpods.rixy.feature.admin.dashboard.AdminDashboardViewModel
import com.externalpods.rixy.feature.admin.moderation.ModerationViewModel
import com.externalpods.rixy.feature.admin.users.UsersManagementViewModel
import com.externalpods.rixy.feature.auth.LoginViewModel
import com.externalpods.rixy.feature.auth.RegisterViewModel
import com.externalpods.rixy.feature.owner.business.BusinessEditorViewModel
import com.externalpods.rixy.feature.owner.cityslots.OwnerCitySlotsViewModel
import com.externalpods.rixy.feature.owner.dashboard.OwnerDashboardViewModel
import com.externalpods.rixy.feature.owner.featured.FeaturedCampaignsViewModel
import com.externalpods.rixy.feature.owner.listings.ListingEditorViewModel
import com.externalpods.rixy.feature.owner.listings.OwnerListingsViewModel
import com.externalpods.rixy.feature.settings.SettingsViewModel
import com.externalpods.rixy.feature.user.browse.BrowseListingsViewModel
// FavoritesViewModel is defined in AppModule
import com.externalpods.rixy.feature.user.businessprofile.BusinessProfileViewModel
import com.externalpods.rixy.feature.user.cityhome.CityHomeViewModel
import com.externalpods.rixy.feature.user.cityselector.CitySelectorViewModel
import com.externalpods.rixy.feature.user.listingdetail.ListingDetailViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val featureModule = module {
    
    // Auth
    viewModelOf(::LoginViewModel)
    viewModel { RegisterViewModel(get()) }
    
    // User Mode
    viewModelOf(::CitySelectorViewModel)
    viewModelOf(::CityHomeViewModel)
    viewModel { params ->
        ListingDetailViewModel(
            getListingDetailUseCase = get(),
            analyticsService = get(),
            citySlug = params.get(),
            listingId = params.get()
        )
    }
    viewModel { params ->
        BusinessProfileViewModel(
            getBusinessUseCase = get(),
            listingRepository = get(),
            analyticsService = get(),
            citySlug = params.get(),
            businessId = params.get()
        )
    }
    viewModel { params ->
        BrowseListingsViewModel(
            getListingsUseCase = get(),
            ownerRepository = get(),
            favoritesRepository = get(),
            appState = get(),
            citySlugParam = params.getOrNull<String>()
        )
    }
    // Note: AppStateViewModel is injected where AppState was used before
    // FavoritesViewModel already defined in AppModule
    
    // Owner Mode
    viewModelOf(::OwnerDashboardViewModel)
    viewModel { BusinessEditorViewModel(get(), get(), get()) }
    viewModel { params -> ListingEditorViewModel(get(), get(), params.getOrNull<String>()) }
    viewModelOf(::FeaturedCampaignsViewModel)
    viewModelOf(::OwnerCitySlotsViewModel)
    viewModelOf(::OwnerListingsViewModel)
    
    // Admin Mode
    viewModelOf(::AdminDashboardViewModel)
    viewModelOf(::ModerationViewModel)
    viewModelOf(::CitiesManagementViewModel)
    viewModelOf(::UsersManagementViewModel)
    viewModelOf(::AuditLogsViewModel)
    
    // Settings
    viewModelOf(::SettingsViewModel)
}
