package com.example.fooddeliveryapp.retrofit

import com.example.fooddeliveryapp.Entities.Menu
import com.example.fooddeliveryapp.cnst.url
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    const val base_url:String = url

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(base_url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val restaurantsService:RestaurantsService by lazy {
        retrofit.create(RestaurantsService::class.java)
    }

    val menusService:MenuService by lazy {
        retrofit.create(MenuService::class.java)
    }

    val userServices:UserServices by lazy {
        retrofit.create(UserServices::class.java)
    }

}