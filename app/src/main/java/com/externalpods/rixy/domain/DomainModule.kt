package com.externalpods.rixy.domain

import com.externalpods.rixy.domain.usecase.admin.ModerateBusinessUseCase
import com.externalpods.rixy.domain.usecase.admin.ModerateListingUseCase
import com.externalpods.rixy.domain.usecase.business.CreateBusinessUseCase
import com.externalpods.rixy.domain.usecase.business.GetBusinessUseCase
import com.externalpods.rixy.domain.usecase.business.UpdateBusinessUseCase
import com.externalpods.rixy.domain.usecase.city.GetCitiesUseCase
import com.externalpods.rixy.domain.usecase.city.GetCityHomeUseCase
import com.externalpods.rixy.domain.usecase.listing.*
import com.externalpods.rixy.domain.usecase.owner.GetAnalyticsUseCase
import com.externalpods.rixy.domain.usecase.owner.GetOwnerProfileUseCase
import org.koin.dsl.module

val domainModule = module {
    // City
    factory { GetCitiesUseCase(get()) }
    factory { GetCityHomeUseCase(get()) }

    // Listing
    factory { GetListingsUseCase(get()) }
    factory { GetListingDetailUseCase(get(), get()) }
    factory { CreateListingUseCase(get()) }
    factory { UpdateListingUseCase(get()) }
    factory { DeleteListingUseCase(get()) }

    // Business
    factory { GetBusinessUseCase(get(), get(), get()) }
    factory { CreateBusinessUseCase(get()) }
    factory { UpdateBusinessUseCase(get()) }

    // Owner
    factory { GetOwnerProfileUseCase(get()) }
    factory { GetAnalyticsUseCase(get()) }

    // Admin
    factory { ModerateListingUseCase(get()) }
    factory { ModerateBusinessUseCase(get()) }
}
