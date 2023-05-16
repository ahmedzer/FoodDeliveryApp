package com.example.fooddeliveryapp.DB.Dao

import androidx.room.Entity


@Entity(tableName = "card_menus", primaryKeys = ["id_menu","id_rest"])
data class CartMenu(var id_menu:Int,var id_rest:Int,var nom_menu:String,var qte:Int,var prix:Double)