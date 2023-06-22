package com.albrodiaz.locationexample.data

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class LocationService @Inject constructor(
    private val context: Context,
    private val locationClient: FusedLocationProviderClient
) {
    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.S)
    fun requestLocationUpdates(): Flow<Location?> = callbackFlow {

        locationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                trySend(location)
                Log.i("alberto", "onSuccess: $location")
            }
            .addOnFailureListener {
                trySend(null)
            }

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                locationResult.locations.lastOrNull()?.let {
                    trySend(it)
                    Log.i("alberto", "onLocationResult: $it")
                }
            }
        }

        awaitClose {
            locationClient.removeLocationUpdates(locationCallback)
        }
    }

}


