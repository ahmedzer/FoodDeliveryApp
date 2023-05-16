package com.example.fooddeliveryapp.Repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.fooddeliveryapp.Entities.Restaurant
import com.example.fooddeliveryapp.retrofit.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class RestaurantRepository {

    private val restaurantsService = ApiClient.restaurantsService
    private val loadingLiveData = MutableLiveData<Boolean>()

    fun getRestaurants(viewModelScope: CoroutineScope):LiveData<List<Restaurant>> {
        val  RestaurantLiveData= MutableLiveData<List<Restaurant>>()
        viewModelScope.launch {
            try {
                loadingLiveData.postValue(true)
                val restaurants = restaurantsService.getAllRestaurants()
                RestaurantLiveData.postValue(restaurants)
            }
            catch (e:Exception) {
                Log.d("User Respo error",e.message.toString())
            }
            finally {
                loadingLiveData.postValue(false)
            }
        }
        return RestaurantLiveData
    }

    fun isLoading(): LiveData<Boolean> {
        return loadingLiveData
    }
}