package com.albrodiaz.locationexample

import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.albrodiaz.locationexample.domain.GetLocationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.S)
@HiltViewModel
class MainActivityVM @Inject constructor(
    getLocationUseCase: GetLocationUseCase
) : ViewModel() {

    val location: StateFlow<ViewState> =
        getLocationUseCase.invoke().map(ViewState::Success)
            .catch { error -> error.printStackTrace() }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ViewState.Loading)


}

sealed interface ViewState {
    object Loading : ViewState
    data class Success(val location: Location?) : ViewState
}