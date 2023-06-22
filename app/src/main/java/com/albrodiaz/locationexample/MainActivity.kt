package com.albrodiaz.locationexample

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.albrodiaz.locationexample.extension.hasLocationPermission
import com.albrodiaz.locationexample.ui.theme.LocationExampleTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("MissingPermission")
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val permissions = listOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )

            val permissionState = rememberMultiplePermissionsState(permissions = permissions)
            val locationViewModel: MainActivityVM = hiltViewModel()

            val location by locationViewModel.location.collectAsState()

            LocationExampleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    LaunchedEffect(!this.hasLocationPermission()) {
                        permissionState.launchMultiplePermissionRequest()
                    }

                    if (permissionState.shouldShowRationale) {
                        RationaleAlert(onDismiss = { }) {
                            permissionState.launchMultiplePermissionRequest()
                        }
                    }

                    with(location) {
                        when (this) {
                            ViewState.Loading -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }

                            is ViewState.Success -> {
                                val currentLoc =
                                    LatLng(
                                        this.location?.latitude ?: 0.0,
                                        this.location?.longitude ?: 0.0
                                    )
                                MainScreen(
                                    currentPosition = LatLng(
                                        currentLoc.latitude,
                                        currentLoc.longitude
                                    ),
                                    cameraState = rememberCameraPositionState {
                                        position = CameraPosition.fromLatLngZoom(
                                            currentLoc,
                                            15f
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreen(currentPosition: LatLng, cameraState: CameraPositionState) {
    val marker = LatLng(currentPosition.latitude, currentPosition.longitude)
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraState
    ) {
        Marker(state = MarkerState(position = marker), title = "Albrodiaz", snippet = "Marker")
    }
}

@Composable
fun RationaleAlert(onDismiss: () -> Unit, onConfirm: () -> Unit) {

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties()
    ) {
        Surface(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Necesitamos los permisos de localización para poder utilizar esta aplicación",
                )
                Spacer(modifier = Modifier.height(24.dp))
                TextButton(
                    onClick = {
                        onConfirm()
                        onDismiss()
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("OK")
                }
            }
        }
    }
}