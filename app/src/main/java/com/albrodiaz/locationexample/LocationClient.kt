package com.albrodiaz.locationexample

import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow

interface LocationClient {

    fun requestLocationUpdates(interval: Long): Flow<LatLng>

    class LocationException(message: String) : Exception()
}