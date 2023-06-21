package com.example.fooddeliveryapp.retrofit

import androidx.lifecycle.LiveData
import com.example.fooddeliveryapp.Entities.Commande

import com.example.fooddeliveryapp.Entities.Rating
import com.example.fooddeliveryapp.Entities.User
import retrofit2.Response
import retrofit2.http.*

interface UserServices {


    @POST("users/insert")
    suspend fun insertUser(@Body user: User): Response<Unit>

    @POST("command/insert")
    suspend fun insertCommand(@Body commande: Commande):Response<Unit>

    @POST("users/insertRating/{user_id}/{rating}/{id_rest}")
    suspend fun addRating(
        @Path("user_id") user_id: Int,
        @Path("rating") rating: Int,
        @Path("id_rest") id_rest: Int
    ): Response<Unit>

    @GET("users/getuser/{email}")
    suspend fun getUser(@Path("email") mail: String): Response<User>

    @GET("users/login")
    suspend fun logUser(
        @Query("email") email: String,
        @Query("password") password: String
    ): User

    @POST("notif/{id_user}/{token}")
    suspend fun insertNewToken(@Path("id_user")id_user:Int,@Path("token")token:String):Response<Unit>

    @GET("rest_rating/{id_rest}")
    suspend fun getAverageRating(@Path("id_rest")id_rest:Int): List<Rating>
}