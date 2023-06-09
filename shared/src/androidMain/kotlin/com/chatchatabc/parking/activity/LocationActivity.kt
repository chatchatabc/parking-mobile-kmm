package com.chatchatabc.parking.activity

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import com.chatchatabc.parkingadmin.android.service.LocationService
import timber.log.Timber


/**
 * # LocationActivity
 *
 * This activity class handles majority of the permission work related to location.
 */
abstract class LocationActivity : ComponentActivity() {
    /**
     * This flow will emit true if the permission is granted, false otherwise.
     * Use this flow to conditionally enable features that require location to work.
     */
    val grantedPermissions: MutableLiveData<List<String>> = MutableLiveData(listOf())

    fun withLocationPermission(onPermissionGranted: () -> Unit) {
        Timber.d("Checking for permission...")
        if (checkLocationPermission()) {
            Timber.d("Permission granted!")
            // Location permission granted, run the lambda
            onPermissionGranted()
        } else {
            requestLocationPermission {
                if (it) {
                    onPermissionGranted()
                } else {
                    Timber.d("LocationActivity", "Permission not granted!")
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, ACCESS_FINE_LOCATION)) {
                        // Show rationale and request permission
                        Timber.d("LocationActivity", "Showing rationale dialog")
                        showRationaleDialog {
                            Timber.d("Rationale dialog dismissed, requesting permission")
                            requestLocationPermission { granted ->
                                if (granted) {
                                    Timber.d("Permission granted!")
                                    onPermissionGranted()
                                } else {
                                    Timber.d("Permission not granted!")
                                    showPermissionRequiredDialog()
                                }
                            }
                        }
                    } else {
                        Timber.d("Permission denied too many times! Showing permission required dialog")
                        // User has previously denied the permission and checked "Don't ask again"
                        showPermissionSettingsDialog()
                    }
                }
            }
        }
    }

    val LOCATION_PERMISSION_REQUEST_CODE = 10

    private fun checkLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED
    }

    private fun requestLocationPermission(onPermissionResult: (granted: Boolean) -> Unit) {
        val requestCode = LOCATION_PERMISSION_REQUEST_CODE
        requestPermissionLauncher.launch(ACCESS_FINE_LOCATION)
        permissionRequestCallbacks[requestCode] = onPermissionResult
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            val callback = permissionRequestCallbacks[LOCATION_PERMISSION_REQUEST_CODE]
            callback?.invoke(isGranted)
        }

    private val permissionRequestCallbacks = mutableMapOf<Int, (Boolean) -> Unit>()

    private fun showRationaleDialog(onPositiveButtonClick: () -> Unit) {
        AlertDialog.Builder(this)
            .setMessage("This feature requires precise location to be enabled.")
            .setPositiveButton("Retry") { _, _ -> onPositiveButtonClick() }
            .setNegativeButton("Back", null).create().show()
    }

    private fun showPermissionRequiredDialog() {
        AlertDialog.Builder(this).setMessage("We need location permission to provide this feature.")
            .setPositiveButton("OK", null).create().show()
    }

    private fun showPermissionSettingsDialog() {
        AlertDialog.Builder(this)
            .setMessage("Location permission is required to use this feature. Please enable it from the app settings.")
            .setPositiveButton("Settings") { _, _ ->
                // Open the app settings screen
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }.setNegativeButton("Cancel", null).create().show()
    }

    val locationService by lazy {
        LocationService(this)
    }
}