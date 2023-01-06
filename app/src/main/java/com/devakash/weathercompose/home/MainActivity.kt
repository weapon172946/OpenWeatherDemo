package com.devakash.weathercompose.home

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.playlocationhelper.PlayLocationHelper
import com.playlocationhelper.utils.LocationFailedEnum
import com.playlocationhelper.utils.PlayLocationListener

class MainActivity : ComponentActivity() {
    private val viewModel: HomeViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeUi(viewModel)
        }
        viewModel.status.value = "Checking Permission"
        playLocationHelper.fetchLocation()

        init()
    }

    private fun init() {
        viewModel.error.observe(this) {
            if (!it.isNullOrBlank())
                if (it == "Success")
                    playLocationHelper.stopUpdates()
                else
                    Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPause() {
        super.onPause()
    }


    var playLocationHelper = PlayLocationHelper(this, object : PlayLocationListener {
        override fun onSuccess(locations: Location) {
            viewModel.status.value = "Fetching From OpenWeather"
            viewModel.hitWeatherApi(locations)
        }

        override fun onFailure(locationFailedEnum: LocationFailedEnum) {
            makeToast(locationFailedEnum.name)
        }
    })

    fun makeToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        viewModel.status.value = "Fetching Location"
        playLocationHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        viewModel.status.value = "Parsing Location"
        playLocationHelper.onActivityResult(requestCode, resultCode, data)
    }

}
