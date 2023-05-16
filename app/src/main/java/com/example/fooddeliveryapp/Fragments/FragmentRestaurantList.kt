package com.example.fooddeliveryapp.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fooddeliveryapp.Adapters.RestaurantAdapter
import com.example.fooddeliveryapp.Entities.Restaurant
import com.example.fooddeliveryapp.R
import com.example.fooddeliveryapp.retrofit.Endpoint
import com.example.fooddeliveryapp.viewmodel.RestaurantListViewModel
import kotlinx.coroutines.*


class FragmentRestaurantList : Fragment() {

    lateinit var restaurantVM:RestaurantListViewModel
    lateinit var restaurantRecyclerView:RecyclerView
    lateinit var pBar:ProgressBar
    lateinit var restaurantListAdapter:RestaurantAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_restaurant_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        restaurantRecyclerView = requireActivity().findViewById<RecyclerView>(R.id.rest_recyclerView)
        pBar = requireActivity().findViewById<ProgressBar>(R.id.progressBar)

        restaurantVM = ViewModelProvider(requireActivity()).get(RestaurantListViewModel::class.java)

        restaurantRecyclerView.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.VERTICAL,false)

        restaurantListAdapter = RestaurantAdapter(requireContext(), findNavController())
        restaurantRecyclerView.adapter = restaurantListAdapter


        //Loading live restaurants data
        restaurantVM.getRestaurants().observe(viewLifecycleOwner) {
            restaurantListAdapter.submitData(it)

        }
        //control progress bar visiblity
        restaurantVM.isLoading().observe(viewLifecycleOwner) {
            pBar.visibility = if (it) View.VISIBLE else View.GONE
        }

    }


}