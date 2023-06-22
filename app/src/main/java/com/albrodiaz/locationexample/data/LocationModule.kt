package com.albrodiaz.locationexample.data

import android.content.Context
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object LocationModule {

    @Singleton
    @Provides
    fun provideLocationClient(
        @ApplicationContext context: Context
    ) = LocationService(
        context,
        LocationServices.getFusedLocationProviderClient(context)
    )
}