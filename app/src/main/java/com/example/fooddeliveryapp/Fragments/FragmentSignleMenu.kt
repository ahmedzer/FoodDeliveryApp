package com.example.fooddeliveryapp.Fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.app.Dialog
import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteException
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.fooddeliveryapp.DB.Dao.AppDataBase
import com.example.fooddeliveryapp.DB.Dao.CartMenu
import com.example.fooddeliveryapp.Entities.Menu
import com.example.fooddeliveryapp.Entities.Restaurant
import com.example.fooddeliveryapp.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.zip.DeflaterInputStream


/**
 * A simple [Fragment] subclass.
 * Use the [FragmentSignleMenu.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentSignleMenu : Fragment() {

    private lateinit var appDB: AppDataBase


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signle_menu, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appDB = AppDataBase.buildDataBase(requireContext())!!

        val menuImg = requireActivity().findViewById<ImageView>(R.id.MenuImg)
        val menuName = requireActivity().findViewById<TextView>(R.id.Menu_name)
        val menuDescription = requireActivity().findViewById<TextView>(R.id.menu_description)
        val menuPrix = requireActivity().findViewById<TextView>(R.id.menu_Prix)
        val menuRating = requireActivity().findViewById<TextView>(R.id.menuRate)
        val menuType = requireActivity().findViewById<TextView>(R.id.Menu_type)
        val addToCard = requireActivity().findViewById<Button>(R.id.ajouterAuPanier)

        val menu: Menu = arguments?.getSerializable("menu") as Menu


        menuImg.setImageResource(menu.menuImage)
        menuDescription.setText(menu.descriptionMenu)
        menuRating.setText(menu.ratingMenu.toString())
        menuName.setText(menu.nomMenu)
        menuPrix.setText(menu.prixMenu.toString() + " DA")
        menuType.setText(menu.typeMenu)

        val menuExists = checkIfMenuExists(menu.id_menu,menu.id_restaurant)


        if(menuExists) {
            addToCard.setText("Retirer du panier")
        }
        else {
            addToCard.setText("Ajouter au panier")
        }



        addToCard.setOnClickListener {
            if(addToCard.text.toString().equals("Ajouter au panier")) {
                showConfirmAddDialog(menu)
            }
            else {
                showDeleteDialog(menu)
            }
        }
    }

    fun addMenuToCard(CartMenu: CartMenu) {


        GlobalScope.launch(Dispatchers.IO) {
            try {
                appDB.getMenuCardDao().insertToCart(CartMenu)
                requireActivity().runOnUiThread {
                    updateUIAfterAdd()
                }
            }
            catch (e:SQLiteConstraintException) {
                Toast.makeText(requireContext(),"Erreur de l'ajout",Toast.LENGTH_SHORT).show()
            }
            val CartMenus = appDB.getMenuCardDao().getAllCartMenus()
            Log.d("BDDDDDDDDDDDDDDDDD",CartMenus.toString())
        }

    }

    fun checkIfMenuExists(id_menu:Int,id_rest:Int):Boolean {

        val CartMenus = appDB.getMenuCardDao().getAllCartMenus()
        for(cm in CartMenus) {
            if(cm.id_menu==id_menu && cm.id_rest==id_rest) return true
        }
        return false
    }

    fun getCartMenu(menu: Menu):CartMenu {
        return appDB.getMenuCardDao().getCartMenu(menu.id_menu,menu.id_restaurant)
    }

    fun updateUIAfterAdd() {
        Toast.makeText(requireContext(),"Menu ajouté avec succès",Toast.LENGTH_SHORT).show()
        val addToCard = requireActivity().findViewById<Button>(R.id.ajouterAuPanier)
        addToCard.setText("Retirer du panier")
    }

    fun updateUIAfterRemove() {
        Toast.makeText(requireContext(),"Menu supprimé avec succès",Toast.LENGTH_SHORT).show()
        val addToCard = requireActivity().findViewById<Button>(R.id.ajouterAuPanier)
        addToCard.setText("Ajouter au panier")
    }



    fun removeFromCard(CartMenu: CartMenu) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                appDB.getMenuCardDao().deleteCartMenu(CartMenu)
                requireActivity().runOnUiThread {
                    updateUIAfterRemove()
                }
            }
            catch (e:SQLiteConstraintException) {
                Toast.makeText(requireContext(),"Erreur de l'ajout",Toast.LENGTH_SHORT).show()
            }
            catch (e:SQLiteException) {
                Toast.makeText(requireContext(),"Erreur de l'ajout",Toast.LENGTH_SHORT).show()

            }

        }
    }

    fun showConfirmAddDialog(menu: Menu) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.add_to_card_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val quantityInput = dialog.findViewById<EditText>(R.id.qte_menu)
        val addBtn = dialog.findViewById<Button>(R.id.confirm_add)
        val cancelBtn = dialog.findViewById<Button>(R.id.cancel_add)

        addBtn.setOnClickListener {

            if(!quantityInput.text.toString().equals("")) {
                val qte = quantityInput.text.toString().toInt()
                if(qte!=0) {
                    val CartMenu = CartMenu(menu.id_menu,menu.id_restaurant,menu.nomMenu,qte,menu.prixMenu)
                    addMenuToCard(CartMenu)
                    dialog.dismiss()
                }
                else {
                    requireActivity().runOnUiThread {
                        Toast.makeText(dialog.context,"Entrez une quantité valide svp",Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else {
                requireActivity().runOnUiThread {
                    Toast.makeText(dialog.context,"Entrez une quantité valide svp",Toast.LENGTH_SHORT).show()
                }
            }
        }
        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()

    }


    fun showDeleteDialog(menu: Menu) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_remove_from_card)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val yesBtn = dialog.findViewById<Button>(R.id.removeFromCard)
        val cancelBut = dialog.findViewById<Button>(R.id.cancelRemove)

        yesBtn.setOnClickListener {
            val CartMenu = getCartMenu(menu)
            removeFromCard(CartMenu)
            dialog.dismiss()
        }
        cancelBut.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

}



