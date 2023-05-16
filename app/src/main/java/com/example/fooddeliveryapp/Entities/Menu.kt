package com.example.fooddeliveryapp.Entities

import android.R.id
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(tableName = "menus")
data class Menu(@PrimaryKey(autoGenerate = true)
                val id_menu:Int,
                val id_restaurant:Int,
                val nomMenu:String,
                val descriptionMenu:String,
                val prixMenu:Double,
                val typeMenu:String,
                val menuImage:Int,
                val ratingMenu:Double):java.io.Serializable