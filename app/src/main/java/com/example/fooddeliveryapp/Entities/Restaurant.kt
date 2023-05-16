package com.example.fooddeliveryapp.Entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


data class Restaurant(
                      val id_rest:Int,
                      val name:String,
                      val img_logo:String,
                      val description:String,
                      val location:String,
                      val facebookLink:String,
                      val facebookWebLink:String,
                      val InstLink:String,
                      val mail:String,
                      val phoneNumber:String,
                      val foodTyp:String

):Serializable
