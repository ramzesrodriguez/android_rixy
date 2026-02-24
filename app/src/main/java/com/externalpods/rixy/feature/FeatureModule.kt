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
import com.externalpods.rixy.feature.settings.SettingsViewModel
import com.externalpods.rixy.feature.user.browse.BrowseListingsViewModel
import com.externalpods.rixy.feature.user.favorites.FavoritesViewModel
import com.externalpods.rixy.feature.user.businessprofile.BusinessProfileViewModel
import com.externalpods.rixy.feature.user.cityhome.CityHomeViewModel
import com.externalpods.rixy.feature.user.cityselector.CitySelectorViewModel
import com.externalpods.rixy.feature.user.listingdetail.ListingDetailViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val featureModule = module {
    
    // Auth
    viewModel { LoginViewModel(get(), get()) }
    viewModel { RegisterViewModel(get()) }
    
    // User Mode
    viewModel { CitySelectorViewModel(get(), get()) }
    viewModel { CityHomeViewModel(get(), get(), get()) }
    viewModel { ListingDetailViewModel(get(), get(), get()) }
    viewModel { BusinessProfileViewModel(get(), get(), get(), get()) }
    viewModel { BrowseListingsViewModel(get(), get()) }
    viewModel { FavoritesViewModel(get()) }
    
    // Owner Mode
    viewModel { OwnerDashboardViewModel(get(), get()) }
    viewModel { BusinessEditorViewModel(get(), get()) }
    viewModel { params -> ListingEditorViewModel(get(), get(), params.getOrNull<String>()) }
    viewModel { FeaturedCampaignsViewModel(get()) }
    viewModel { OwnerCitySlotsViewModel(get(), get()) }
    
    // Admin Mode
    viewModel { AdminDashboardViewModel(get()) }
    viewModel { ModerationViewModel(get()) }
    viewModel { CitiesManagementViewModel(get()) }
    viewModel { UsersManagementViewModel(get()) }
    viewModel { AuditLogsViewModel(get()) }
    
    // Settings
    viewModel { SettingsViewModel(get(), get()) }
}
