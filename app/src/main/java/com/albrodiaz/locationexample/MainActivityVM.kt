package com.albrodiaz.locationexample

import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.albrodiaz.locationexample.domain.GetLocationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.S)
@HiltViewModel
class MainActivityVM @Inject constructor(
    private val getLocationUseCase: GetLocationUseCase
) : ViewModel() {

    val viewState: MutableStateFlow<ViewState> = MutableStateFlow(ViewState.Loading)

    fun handle(event: PermissionEvent) {
        when (event) {
            PermissionEvent.Granted -> {
                viewModelScope.launch {
                    getLocationUseCase.invoke().collect {
                        viewState.value = ViewState.Success(it)
                    }
                }
            }

            PermissionEvent.Revoked -> {
                viewState.value = ViewState.RevokedPermissions
            }
        }
    }

}

sealed interface ViewState {
    object Loading : ViewState
    data class Success(val location: Location?) : ViewState
    object RevokedPermissions : ViewState
}

sealed interface PermissionEvent {
    object Granted : PermissionEvent
    object Revoked : PermissionEvent
}