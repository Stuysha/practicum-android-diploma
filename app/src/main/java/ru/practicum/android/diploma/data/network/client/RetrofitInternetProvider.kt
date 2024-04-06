package ru.practicum.android.diploma.data.network.client

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.practicum.android.diploma.BuildConfig
import ru.practicum.android.diploma.util.Constants

object RetrofitInternetProvider {
    val retrofit = Retrofit.Builder()
        .baseUrl(Constants.HH_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(
            OkHttpClient.Builder()
                .apply {
                    addInterceptor {
                        val request = it.request().newBuilder().apply {
                            addHeader("Authorization", "Bearer ${BuildConfig.HH_ACCESS_TOKEN}")
                            addHeader("HH-User-Agent", "Application Name (riabikina5@gmail.com)")
                        }.build()
                        it.proceed(request)
                    }
                    addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                }
                .build()
        )
        .build()
}
