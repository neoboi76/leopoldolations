package com.gabriel.employeeapp.di

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://10.1.10.102:8080/"
//   private const val BASE_URL = "http://192.168.5.101:8080/"

 //   private const val BASE_URL = "http://172.29.128.1:8080/"

    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

}