package com.example.fooddeliveryapp.retrofit

import com.example.fooddeliveryapp.Entities.Restaurant
import retrofit2.Response
import retrofit2.http.GET

interface RestaurantsService {

    @GET("restaurants/getall")
    suspend fun getAllRestaurants(): List<Restaurant>
}