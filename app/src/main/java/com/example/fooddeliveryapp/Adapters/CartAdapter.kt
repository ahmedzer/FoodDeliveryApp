package com.example.fooddeliveryapp.Adapters

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fooddeliveryapp.DB.Dao.AppDataBase
import com.example.fooddeliveryapp.DB.Dao.CartMenu
import com.example.fooddeliveryapp.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CartAdapter(var context:Context,var cartMenuList:ArrayList<CartMenu>):RecyclerView.Adapter<CartAdapter.ViewHolder>() {
    class ViewHolder(itemV: View):RecyclerView.ViewHolder(itemV) {
        val menuQte = itemV.findViewById<TextView>(R.id.cartMenuQte)
        val menuNom = itemV.findViewById<TextView>(R.id.cartMenuName)
        val menuPrix = itemV.findViewById<TextView>(R.id.cartMenuPrix)
        val menuDeleteBtn = itemV.findViewById<Button>(R.id.cartMenuDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.card_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return  cartMenuList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.menuNom.setText(cartMenuList[position].nom_menu)
        holder.menuPrix.setText("Prix unitaire : "+cartMenuList[position].prix.toString()+" DA")
        holder.menuQte.setText("Quantité : "+cartMenuList[position].qte.toString())
        holder.menuDeleteBtn.setOnClickListener {
            showDeleteDialog(cartMenuList[position])
        }
    }



    fun supprimerCartMenu(cartMenu: CartMenu) {
        val appDB = AppDataBase.buildDataBase(context)!!
        cartMenuList.remove(cartMenu)
        this.notifyDataSetChanged()
        GlobalScope.launch(Dispatchers.IO) {
            appDB.getMenuCardDao().deleteCartMenu(cartMenu)
        }
    }

    fun showDeleteDialog(cartMenu: CartMenu) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_remove_from_card)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val yesBtn = dialog.findViewById<Button>(R.id.removeFromCard)
        val cancelBut = dialog.findViewById<Button>(R.id.cancelRemove)
        yesBtn.setOnClickListener {
            supprimerCartMenu(cartMenu)
            dialog.dismiss()
        }
        cancelBut.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
}