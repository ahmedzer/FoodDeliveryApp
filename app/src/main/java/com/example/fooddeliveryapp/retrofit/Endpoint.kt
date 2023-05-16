package com.example.fooddeliveryapp.retrofit

import com.example.fooddeliveryapp.Entities.Restaurant
import com.example.fooddeliveryapp.cnst.url
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface Endpoint {

    @GET("restaurants/getall")
    suspend fun getAllRestaurants():Response<List<Restaurant>>

    companion object {
        @Volatile
        var endpoint:Endpoint? = null
        fun createEndpoint():Endpoint {
            if(endpoint==null) {
                endpoint = Retrofit.Builder().baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create()).build()
                    .create(Endpoint::class.java)
            }
            return endpoint!!
        }
    }
}