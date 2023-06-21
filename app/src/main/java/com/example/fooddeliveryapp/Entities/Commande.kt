package com.example.fooddeliveryapp.Entities

data class Commande(val id_command:Int,val id_user:Int,val state:String,val location:String,val info_menu:String,val date:String,val total_prix:Double,val livreur_phone:String)