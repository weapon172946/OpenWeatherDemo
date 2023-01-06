package com.devakash.weathercompose.home

import android.location.Location
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devakash.weathercompose.misc.Utils.API_KEY
import com.devakash.weathercompose.model.CurrentWeatherModel
import com.devakash.weathercompose.web.ApiClient
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    var response = mutableStateOf<CurrentWeatherModel?>(null)
    var status = mutableStateOf<String>("Loading")

    val error: LiveData<String> get() = _error
    var _error = MutableLiveData<String>(null)

    fun hitWeatherApi(locations: Location) {
        viewModelScope.launch {
            val weather = ApiClient.provideRestService()
                .getWeather(
                    locations.latitude.toString(),
                    locations.longitude.toString(),
                    "metric",
                    API_KEY
                )
            if (weather.isSuccessful && weather.body() != null) {
                status.value = "Success"
                _error.value = "Success"
                response.value = weather.body()
            } else {
                status.value = "Something went wrong!"
                _error.postValue(weather.message())
            }
        }
    }
}