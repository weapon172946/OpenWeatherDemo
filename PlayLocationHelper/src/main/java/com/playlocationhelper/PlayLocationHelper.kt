package com.playlocationhelper

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.location.*
import com.playlocationhelper.utils.*

/**
 * @author Akash Saini
 * Last Updated 11feb2022
 * Play location helper Class
 *
 * @property context needed of an activity
 * @property callback should be implemented to receive location
 * @property isLocationRequiredOneTime, If continuous location required then false, default true for one time
 * @property liveGpsMode, default true fetches fresh location may take some seconds, for instant pass false for fused location
 * @property locationIntervalInMillis , for continuous updates in millis
 * @constructor Create empty Play location helper
 */
class PlayLocationHelper(
    private val context: Context,
    private val callback: PlayLocationListener?,
    private val isLocationRequiredOneTime: Boolean = false,
    private val liveGpsMode: Boolean = true,
    private val locationIntervalInMillis: Long = 0
) {
    private lateinit var locationCallback: LocationCallback
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var isStartCalled = false

    /*Executed 1st after Start*/
    private val googlePlayApiHelper = GooglePlayApiHelper(context as Activity, fun() {
        //onSuccess
        getLocationPermissions()
    }, fun() {
        //onFailure
        getLocationPermissions()
//        callback?.onFailure(LocationFailedEnum.GOOGLE_PLAY_API_NOT_AVAILABLE)
    })

    /*Executed 2nd - after 1st onSuccess*/
    private val permissionHelperX = PermissionHelperX(context as Activity, arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ), fun() {
        //onSuccess
        checkIfInFlightMode()
    }, fun() {
        //onFailure
        callback?.onFailure(LocationFailedEnum.LOCATION_PERMISSION_NOT_GRANTED)
    })

    /*Executed 4th - after airplane mode check*/
    private val locationOptimizationPermissionHelper = LocationOptimizationPermissionHelper(
        context as Activity,
        locationIntervalInMillis,
        isLocationRequiredOneTime,
        fun() {
            //onSuccess
            getFusedLocation()
        }, fun(locationFailedEnum: LocationFailedEnum) {
            //onFailure
            callback?.onFailure(locationFailedEnum)
        }
    )


    /**
     * Fetch location
     * call after creating object and remember to pass Activity Result & Permission Result first
     */
    fun fetchLocation() {
        isStartCalled = true
        googlePlayApiHelper.makeItAvailable()
    }


    private fun getLocationPermissions() {
        permissionHelperX.request()
    }

    /*Executed 3rd - After Permission Check*/
    private fun checkIfInFlightMode() {
        if (NetworkHelper.isInFlightMode(context as Activity)) {
            //onFailure
            callback?.onFailure(LocationFailedEnum.DEVICE_IN_FLIGHT_MODE)
        } else {
            //onSuccess
            getOptimizationPermissions()
        }
    }

    private fun getOptimizationPermissions() {
        locationOptimizationPermissionHelper.getPermission()
    }


    /*After all Permission register the location listener*/
    @SuppressLint("MissingPermission")
    private fun getFusedLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        val task = fusedLocationClient.lastLocation
        task.addOnSuccessListener { location: Location? ->
            if (location != null && !liveGpsMode) {
                callback?.onSuccess(location)
                if (isLocationRequiredOneTime.not()) {
                    addLifecycleListener()
                }
            } else {
                addLifecycleListener()
            }
        }.addOnFailureListener {
            addLifecycleListener()
        }
    }

    /**
     * Stop updates
     * Only Required In Case of Continuous Updates
     */
    fun stopUpdates() {
        if (this::fusedLocationClient.isInitialized && this::locationCallback.isInitialized)
            fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun addLifecycleListener() {
        val activityTemp = context ?: return
        (activityTemp as LifecycleOwner).lifecycle.addObserver(object :
            DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                super.onResume(owner)
                requestLocationUpdates()
            }

            override fun onPause(owner: LifecycleOwner) {
                super.onPause(owner)
                if (this@PlayLocationHelper::locationCallback.isInitialized) {
                    Log.e("AkashTag", "fusedListenerRemoved")
                    fusedLocationClient.removeLocationUpdates(locationCallback)
                }
            }

            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                if (this@PlayLocationHelper::locationCallback.isInitialized) {
                    Log.e("AkashTag", "fusedListenerRemoved")
                    fusedLocationClient.removeLocationUpdates(locationCallback)
                }
            }
        })
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates() {

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                Log.e("AkashTag", "fusedLocationClient onLocationResult")

                if (!locationResult.locations.isNullOrEmpty()) {
                    callback?.onSuccess(locationResult.locations[0])
                }
                if (isLocationRequiredOneTime)
                    fusedLocationClient.removeLocationUpdates(locationCallback)

            }

            override fun onLocationAvailability(locationAvailability: LocationAvailability) {
                if (!locationAvailability.isLocationAvailable) {
                    callback?.onFailure(LocationFailedEnum.HIGH_PRECISION_LOCATION_NA_TRY_AGAIN_PREFERABLY_WITH_NETWORK_CONNECTIVITY)
                    fusedLocationClient.removeLocationUpdates(locationCallback)
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            LocationOptimizationPermissionHelper.getLocationRequest(
                locationIntervalInMillis,
                isLocationRequiredOneTime
            ),
            locationCallback,
            Looper.getMainLooper()
        )
        Log.e("AkashTag", "fusedLocationClient Registered")

    }

    /**
     * On request permissions result
     *
     * @param requestCode received from activity method
     * @param permissions received from activity method
     * @param grantResults received from activity method
     */
    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (isStartCalled.not()) {
            return
        }

        permissionHelperX.onRequestPermissionsResult(requestCode)
    }

    /**
     * On activity result
     *
     * @param requestCode received from activity override method
     * @param resultCode received from activity override method
     * @param data received from activity override method
     */

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (isStartCalled.not()) {
            return
        }

        permissionHelperX.onActivityResult(requestCode)
        locationOptimizationPermissionHelper.onActivityResult(requestCode, resultCode, data)
        googlePlayApiHelper.onActivityResult(requestCode)
    }
}