package com.example.fooddeliveryapp.Fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.app.Dialog
import android.content.Context
import android.content.Intent
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
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.fooddeliveryapp.Auth.LogIn
import com.example.fooddeliveryapp.DB.Dao.AppDataBase
import com.example.fooddeliveryapp.DB.Dao.CartMenu
import com.example.fooddeliveryapp.Entities.Menu
import com.example.fooddeliveryapp.Entities.MenuRating
import com.example.fooddeliveryapp.Entities.Restaurant
import com.example.fooddeliveryapp.R
import com.example.fooddeliveryapp.cnst.url
import com.example.fooddeliveryapp.viewmodel.MenuListViewModel
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
    lateinit var menuVM:MenuListViewModel


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
        val evaluateBtn = requireActivity().findViewById<Button>(R.id.menu_rate)

        val menu: Menu = arguments?.getSerializable("menu") as Menu
        menuVM = ViewModelProvider(requireActivity()).get(MenuListViewModel::class.java)




        Glide.with(requireActivity()).load(url +menu.menuImage).into(menuImg)
        menuDescription.setText(menu.descriptionMenu)

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
        evaluateBtn.setOnClickListener {
            if(checkUserAuth()) {
                showRatingDialog(menu.id_restaurant,menu.id_menu)
            }
            else {
                val intnt = Intent(requireContext(), LogIn::class.java)
                startActivity(intnt)
            }
        }

        /////////////////// get evaluation
        menuVM.getMenuRating(menu.id_menu,menu.id_restaurant).observe(requireActivity()){
            var avgRating:Int = 0
            for(x in it) {
                avgRating = avgRating+x.rating
            }
            if(it.size!=0) {
                avgRating = avgRating/it.size
            }
            else{
                avgRating = 0
            }
            menuRating.setText(avgRating.toString())
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
                    if(restaurantValide(menu.id_restaurant)) {
                        addMenuToCard(CartMenu)
                        dialog.dismiss()
                    }
                    else {
                        Toast.makeText(dialog.context,"Votre panier contient une commande d'un autre restaurant," +
                                " pour ajouter ce menu valider l'ancien panier ou videz-le",Toast.LENGTH_LONG).show()
                        dialog.dismiss()
                    }
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

    fun getUserId():Int {//recuperer le user_id sauvegardé
        val sharedPrf = requireContext().getSharedPreferences("Auth", Context.MODE_PRIVATE)
        val uid  = sharedPrf.getInt("userId",-1)
        return uid
    }

    fun checkUserAuth():Boolean {//verifier si le user est authentifié
        val sharedPrf = requireContext().getSharedPreferences("Auth", Context.MODE_PRIVATE)
        val editor = sharedPrf.edit()
        val auth = sharedPrf.getBoolean("auth",false)
        return auth
    }


    fun showRatingDialog(id_rest:Int,id_menu: Int) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_rating_res)


        val rateBar = dialog.findViewById<RatingBar>(R.id.ratingBar)
        val submitBut = dialog.findViewById<Button>(R.id.rate_but)

        submitBut.setOnClickListener {
            val rating = rateBar.rating.toInt()
            val uid = getUserId()

            val menuRating = MenuRating(uid,id_rest,id_menu,rating)
            //inserer l'evaluation
            menuVM.insertMenuRating(menuRating)

            menuVM._insertMenuRatingStatus.observe(requireActivity()){
                if(it.isSuccess) {//insertiona avec succès
                    Toast.makeText(requireContext(),"Merci pour votre évaluation ",Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
                else {//erreur
                    Toast.makeText(requireContext(),it.exceptionOrNull()!!.message.toString(),Toast.LENGTH_SHORT).show()
                }
            }
        }
        //afficher le dialogue
        dialog.show()
    }

    fun restaurantValide(id_rest: Int):Boolean{
        val cartMenus = appDB.getMenuCardDao().getAllCartMenus()
        val cartMenuAL = ArrayList(cartMenus)

        for(x in cartMenuAL) {
            if(x.id_rest!=id_rest) {
                return false
            }
        }
        return true
    }

}



