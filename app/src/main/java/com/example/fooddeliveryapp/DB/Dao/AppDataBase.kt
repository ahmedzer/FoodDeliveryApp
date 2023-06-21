package com.example.fooddeliveryapp.DB.Dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.fooddeliveryapp.Entities.Menu


@Database(entities = [Menu::class,CartMenu::class], version = 3)
abstract class AppDataBase:RoomDatabase() {


    abstract fun getMenuDao():MenuDao
    abstract fun getMenuCardDao():CartMenuDao
    companion object {
        private var INSTANCE:AppDataBase?=null
        fun buildDataBase(context: Context):AppDataBase?{
            if(INSTANCE==null) {
                INSTANCE = Room.databaseBuilder(context,AppDataBase::class.java,"app_db")
                    .allowMainThreadQueries().build()
            }
            return INSTANCE
        }
    }
}