package com.devakash.weathercompose.web

import android.util.Log
import com.devakash.weathercompose.BuildConfig
import com.devakash.weathercompose.misc.Utils
import com.google.gson.GsonBuilder
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private val TAG = ApiClient::class.java.simpleName

    private val httpClient = OkHttpClient.Builder()
        .readTimeout((60).toLong(), TimeUnit.SECONDS)
        .connectTimeout((60).toLong(), TimeUnit.SECONDS)
        .writeTimeout((60).toLong(), TimeUnit.SECONDS)
        .addInterceptor(provideHttpLoggingInterceptor())
        .connectionSpecs(listOf(ConnectionSpec.MODERN_TLS, ConnectionSpec.CLEARTEXT)).build()


    private val gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
        .create()

    private val builder = Retrofit.Builder()
        .baseUrl(Utils.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))


    @JvmStatic
    fun <S> createService(serviceClass: Class<S>): S {
        val retrofit = getRetrofit()
        return retrofit.create(serviceClass)
    }

    private var restApiInterface: ApiInterface? = null
    fun provideRestService(): ApiInterface {
        if (restApiInterface == null) restApiInterface = createService(
            ApiInterface::class.java
        )
        return restApiInterface!!
    }

    @JvmStatic
    fun getRetrofit(): Retrofit {
        val client = builder
        return client.addConverterFactory(
            GsonConverterFactory.create(
                GsonBuilder().setPrettyPrinting().create()
            )
        ).build()
    }

    //* HTTP Logging Interceptor
    private fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val httpLoggingInterceptor = HttpLoggingInterceptor { message ->
            Log.d("APICLIENT", "response => " + message)
        }
        httpLoggingInterceptor.level =
            if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        return httpLoggingInterceptor
    }


}