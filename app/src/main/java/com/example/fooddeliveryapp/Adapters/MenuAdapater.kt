package com.example.fooddeliveryapp.Adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fooddeliveryapp.Entities.Menu
import com.example.fooddeliveryapp.Entities.Restaurant
import com.example.fooddeliveryapp.R
import com.example.fooddeliveryapp.cnst.url
import com.example.fooddeliveryapp.viewmodel.MenuListViewModel

class MenuAdapter(var context: Context,var nav:NavController,var menuVM:MenuListViewModel)
    :RecyclerView.Adapter<MenuAdapter.ViewHolder>(){

    private var menuList : MutableList<Menu> = mutableListOf()
    private lateinit var lifecycleOwner: LifecycleOwner

    fun setLifecycleOwner(owner: LifecycleOwner) {
        lifecycleOwner = owner
    }
    class ViewHolder(itemV: View):RecyclerView.ViewHolder(itemV) {
        val menuImg = itemV.findViewById<ImageView>(R.id.menuImg)
        val menuPrix = itemV.findViewById<TextView>(R.id.menuPrix)
        val menuName = itemV.findViewById<TextView>(R.id.menuName)
        val menuDescription = itemV.findViewById<TextView>(R.id.menuDescription)
        val menuRating = itemV.findViewById<TextView>(R.id.menuRating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.menu_item,parent,false))
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context).load(url +menuList[position].menuImage).into(holder.menuImg)
        holder.menuDescription.setText(menuList[position].descriptionMenu)
        holder.menuName.setText(menuList[position].nomMenu)
        holder.menuPrix.setText(menuList[position].prixMenu.toString()+" DA")
        menuVM.getMenuRating(menuList[position].id_menu,menuList[position].id_restaurant).observe(lifecycleOwner){
            var avgRating:Int = 0
            for(x in it) {
                avgRating = avgRating+x.rating
            }
            if(it.size!=0) {
                avgRating = avgRating/it.size
            }
            else{
                avgRating = 0
            }
            holder.menuRating.setText(avgRating.toString())
        }
        holder.itemView.setOnClickListener {
            val args = Bundle()
            args.putSerializable("menu", menuList[position])
            nav.navigate(R.id.action_fragmentMenus_to_fragmentSignleMenu,args)
        }
    }

    fun submitData(menus:List<Menu>) {
        menuList.clear()
        menuList.addAll(menus)
        notifyDataSetChanged()

    }
}