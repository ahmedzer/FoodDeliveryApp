package com.example.fooddeliveryapp.Repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.fooddeliveryapp.Entities.Commande
import com.example.fooddeliveryapp.Entities.Rating
import com.example.fooddeliveryapp.Entities.User
import com.example.fooddeliveryapp.retrofit.ApiClient
import com.example.fooddeliveryapp.retrofit.RestaurantsService
import com.example.fooddeliveryapp.retrofit.UserServices
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.http.Body

class UsersRespository {

    private val userServices = ApiClient.userServices


    suspend fun insertUser(user: User):Response<Unit> {
        val usr = userServices.insertUser(user)
        Log.d("aaaaaaaaaaaaaa",usr.toString())
        return  usr
    }

    suspend fun insertCommand(commande: Commande):Response<Unit>{
        val cmd = userServices.insertCommand(commande)
        Log.d("Command insert",cmd.message().toString())
        return cmd
    }

    suspend fun addRating(userId:Int,rating:Int,id_rest: Int):Response<Unit> {
        val rate = userServices.addRating(userId,rating,id_rest)
        Log.d("insert rate :",rate.message().toString())
        return rate
    }

    suspend fun addToken(id_user:Int,token:String):Response<Unit>{
        val tken = userServices.insertNewToken(id_user,token)
        Log.d("insert token :",tken.message().toString())
        return tken
    }

    suspend fun getUser(email:String):Response<User> {
        val user = userServices.getUser(email)
        return user
    }

    fun logUser(email:String,pass:String,viewModelScope: CoroutineScope):LiveData<User> {
       val user  = MutableLiveData<User>()
        viewModelScope.launch {
            try {
                val usr = userServices.logUser(email,pass)
                Log.d("Repo login :  ",usr.toString())
                user.postValue(usr)
            }
            catch (e:java.lang.Exception) {
                Log.d("User login error : ",e.message.toString())
            }
        }
        return user
    }


    suspend fun getRatings(viewModelScope: CoroutineScope,id_rest:Int):LiveData<List<Rating>>{
        val ratingLiveData = MutableLiveData<List<Rating>>()

        viewModelScope.launch{
            try {
                val ratings = userServices.getAverageRating(id_rest)
                ratingLiveData.postValue(ratings)
            }
            catch (e:java.lang.Exception) {
                Log.d("Menus Respo error",e.message.toString())
            }
        }
        return ratingLiveData
    }


}