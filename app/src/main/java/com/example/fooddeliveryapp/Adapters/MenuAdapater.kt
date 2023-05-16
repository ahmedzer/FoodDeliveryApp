package com.example.fooddeliveryapp.Adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.fooddeliveryapp.Entities.Menu
import com.example.fooddeliveryapp.R

class MenuAdapter(var context: Context,var menuList:ArrayList<Menu>,var nav:NavController)
    :RecyclerView.Adapter<MenuAdapter.ViewHolder>(){
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
        holder.menuImg.setImageResource(menuList[position].menuImage)
        holder.menuDescription.setText(menuList[position].descriptionMenu)
        holder.menuName.setText(menuList[position].nomMenu)
        holder.menuPrix.setText(menuList[position].prixMenu.toString()+" DA")
        holder.menuRating.setText(menuList[position].ratingMenu.toString())

        holder.itemView.setOnClickListener {
            val args = Bundle()
            args.putSerializable("menu", menuList[position])
            nav.navigate(R.id.action_fragmentMenus_to_fragmentSignleMenu,args)
        }
    }
}