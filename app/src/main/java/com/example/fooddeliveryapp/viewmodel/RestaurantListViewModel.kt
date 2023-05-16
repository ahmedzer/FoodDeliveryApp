package com.example.fooddeliveryapp.viewmodel

import androidx.lifecycle.*
import com.example.fooddeliveryapp.Entities.Restaurant
import com.example.fooddeliveryapp.Repository.RestaurantRepository

class RestaurantListViewModel : ViewModel() {
    private val restaurantRepository = RestaurantRepository()


    fun getRestaurants(): LiveData<List<Restaurant>> {
        return liveData {
            val restaurants = restaurantRepository.getRestaurants(viewModelScope)
            emitSource(restaurants)
        }
    }

    fun isLoading(): LiveData<Boolean> {
        return restaurantRepository.isLoading()
    }
}