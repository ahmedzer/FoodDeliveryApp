package com.example.fooddeliveryapp.DB.Dao

import androidx.room.*
import com.example.fooddeliveryapp.Entities.Menu

@Dao
interface MenuDao {
    @Query("select * from menus")
    fun getCardMenus():List<Menu>

    @Insert
    fun insertMenuToCard(menu: Menu)

    @Update
    fun updateMenu(menu: Menu)

    @Delete
    fun deleteMenuFromCard(menu: Menu)

    @Query("select * from menus where id_menu= :idMenu")
    suspend fun getMenuById(idMenu:Int):Menu

    @Query("delete from menus")
    suspend fun deleteAll()
}