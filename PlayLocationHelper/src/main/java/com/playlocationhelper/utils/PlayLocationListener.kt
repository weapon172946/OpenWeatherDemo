package com.playlocationhelper.utils

import android.location.Location

interface PlayLocationListener {
    fun onSuccess(locations: Location)
    fun onFailure(locationFailedEnum: LocationFailedEnum)
}
