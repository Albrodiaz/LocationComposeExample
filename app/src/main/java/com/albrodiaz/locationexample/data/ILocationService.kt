package com.albrodiaz.locationexample.data

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface ILocationService {

    fun requestLocationUpdates(): Flow<Location?>

    fun requestCurrentLocation(): Flow<Location?>
}