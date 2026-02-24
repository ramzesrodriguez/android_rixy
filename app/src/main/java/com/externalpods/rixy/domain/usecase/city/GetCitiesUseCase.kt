package com.externalpods.rixy.domain.usecase.city

import com.externalpods.rixy.core.model.City
import com.externalpods.rixy.data.repository.CityRepository

class GetCitiesUseCase(private val cityRepository: CityRepository) {
    suspend operator fun invoke(activeOnly: Boolean = true): Result<List<City>> {
        return try {
            Result.success(cityRepository.getCities(activeOnly))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
