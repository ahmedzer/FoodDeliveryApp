package com.example.fooddeliveryapp.DB.Dao

import androidx.room.*


@Dao
interface CartMenuDao {
    @Query("select * from card_menus")
    fun getAllCartMenus():List<CartMenu>

    @Insert
    suspend fun insertToCart(cartMenu: CartMenu)

    @Update
    suspend fun updateCartMenu(cardMenu: CartMenu)

    @Delete
    suspend fun deleteCartMenu(cardMenu: CartMenu)

    @Query("delete from card_menus")
    suspend fun deleteAllCart()

    @Query("select *from card_menus where id_menu=:idMenu and id_rest=:idRest ")
    fun getCartMenu(idMenu:Int,idRest:Int):CartMenu
}