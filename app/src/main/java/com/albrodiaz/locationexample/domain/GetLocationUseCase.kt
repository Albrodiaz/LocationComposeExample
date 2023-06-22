package com.albrodiaz.locationexample.domain

import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import com.albrodiaz.locationexample.data.LocationService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLocationUseCase @Inject constructor(
    private val locationService: LocationService
) {

    @RequiresApi(Build.VERSION_CODES.S)
    operator fun invoke(): Flow<Location?> = locationService.requestLocationUpdates()

}