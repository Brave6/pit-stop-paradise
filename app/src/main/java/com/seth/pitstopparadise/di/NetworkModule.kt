package com.seth.pitstopparadise.di

import com.seth.pitstopparadise.retrofit.ApiService
import com.seth.pitstopparadise.retrofit.TrustAllCertificates
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

   // private const val BASE_URL = "http://192.168.1.3:5000/api/"
    private const val BASE_URL = "https://pitsst.onrender.com/api/"

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .followRedirects(true)
            .followSslRedirects(true)
            .sslSocketFactory(
                TrustAllCertificates.sslSocketFactory,
                TrustAllCertificates.trustManager
            )
            .hostnameVerifier { _, _ -> true }
            // Timeout settings for Render cold starts
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .callTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideApiService2(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}
