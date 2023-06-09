package com.example.fooddeliveryapp.Adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fooddeliveryapp.Entities.Rating
import com.example.fooddeliveryapp.Entities.Restaurant
import com.example.fooddeliveryapp.R
import com.example.fooddeliveryapp.Utils.*
import com.example.fooddeliveryapp.cnst.url
import com.example.fooddeliveryapp.viewmodel.RestaurantListViewModel
import com.example.fooddeliveryapp.viewmodel.UsersViewModel


class RestaurantAdapter(var context: Context,var navController: NavController,var usersViewModel: UsersViewModel):RecyclerView.Adapter<RestaurantAdapter.ViewHolder>() {

    private var rest_list : MutableList<Restaurant> = mutableListOf()
    private var rating_list :MutableList<Rating> = mutableListOf()

    private lateinit var lifecycleOwner: LifecycleOwner

    fun setLifecycleOwner(owner: LifecycleOwner) {
        lifecycleOwner = owner
    }
    class ViewHolder(itemV: View):RecyclerView.ViewHolder(itemV) {

        val resImg = itemV.findViewById<ImageView>(R.id.logo_res)
        val resName = itemV.findViewById<TextView>(R.id.nom_text)
        val resRating = itemV.findViewById<TextView>(R.id.rating_text)
        val resReviews = itemV.findViewById<TextView>(R.id.reviews_text)
        val resFoodType = itemV.findViewById<TextView>(R.id.food_type_text)
        val resLocation = itemV.findViewById<TextView>(R.id.location_txt)
        val googleButton = itemV.findViewById<Button>(R.id.google_link)

        val mapButton = itemV.findViewById<Button>(R.id.map_link)
        val facebookButton = itemV.findViewById<Button>(R.id.fbk_link)
        val phoneButton = itemV.findViewById<Button>(R.id.phone_link)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RestaurantAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.restaurant_item,parent,false))
    }

    override fun onBindViewHolder(holder: RestaurantAdapter.ViewHolder, position: Int) {
        holder.resName.setText(rest_list[position].name)
        Glide.with(context).load(url+rest_list[position].img_logo).into(holder.resImg)
        holder.resLocation.setText(rest_list[position].description)


        //val reviewsText = prettyCount(rest_list[position].review)+"00 avis"

        holder.resFoodType.setText(rest_list[position].foodTyp)

        usersViewModel.getRatings(rest_list[position].id_rest).observe(lifecycleOwner) {
            var rateAvg:Int = 0
            for(x in it) {
                rateAvg += x.rating
            }
            if(it.size!=0) {
                rateAvg /= it.size
            }else {
                rateAvg = 0
            }
            holder.resRating.setText(rateAvg.toString())
            holder.resReviews.setText(prettyCount(it.size) +" avis")
        }
        holder.itemView.setOnClickListener {
            val args = Bundle()
            args.putSerializable("restaurant", rest_list[position])
            navController.navigate(R.id.action_fragmentRestaurantList_to_fragmentMenus,args)
        }

        holder.facebookButton.setOnClickListener {
            openPage(context,rest_list[position].facebookLink,rest_list[position].facebookWebLink)
        }

        holder.mapButton.setOnClickListener {
            openMap(rest_list[position].location,context)
        }

        holder.googleButton.setOnClickListener {
            openMail(context,rest_list[position].mail)
        }

        holder.phoneButton.setOnClickListener {
            openPhone(context,rest_list[position].phoneNumber)
        }
    }

    override fun getItemCount(): Int {
        return rest_list.size
    }

    fun submitData(rests:List<Restaurant>) {
        rest_list.clear()
        rest_list.addAll(rests)
        notifyDataSetChanged()

    }
}