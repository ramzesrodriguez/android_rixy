package com.externalpods.rixy.domain.usecase.city

import com.externalpods.rixy.core.model.CityHome
import com.externalpods.rixy.data.repository.CityRepository

class GetCityHomeUseCase(private val cityRepository: CityRepository) {
    suspend operator fun invoke(citySlug: String): Result<CityHome> {
        return try {
            Result.success(cityRepository.getCityHome(citySlug))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
