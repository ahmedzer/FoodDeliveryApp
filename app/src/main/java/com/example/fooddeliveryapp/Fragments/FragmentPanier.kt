package com.example.fooddeliveryapp.Fragments

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fooddeliveryapp.Adapters.CartAdapter
import com.example.fooddeliveryapp.Auth.LogIn
import com.example.fooddeliveryapp.DB.Dao.AppDataBase
import com.example.fooddeliveryapp.DB.Dao.CartMenu
import com.example.fooddeliveryapp.Entities.Menu
import com.example.fooddeliveryapp.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Collections


class FragmentPanier : Fragment() {
    private lateinit var appDB: AppDataBase
    private lateinit var cartRV:RecyclerView
    private lateinit var CartMenus:List<CartMenu>
    private lateinit var cartMenuAL:ArrayList<CartMenu>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_panier, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appDB = AppDataBase.buildDataBase(requireContext())!!

        cartRV = requireActivity().findViewById<RecyclerView>(R.id.cartRV)
        val viderPanierBtn = requireActivity().findViewById<Button>(R.id.viderPanier)
        val validerPanierBtn = requireActivity().findViewById<Button>(R.id.validerPanier)
        val totalPrixText = requireActivity().findViewById<TextView>(R.id.prixTotal)


        CartMenus = appDB.getMenuCardDao().getAllCartMenus()

        val totalPrix:Double = getPrixTotal(CartMenus)
        totalPrixText.setText("Prix total : "+totalPrix.toString()+" da")
        cartMenuAL = ArrayList(CartMenus)
        cartRV.adapter = CartAdapter(requireContext(),cartMenuAL)
        cartRV.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        updateUI()


        viderPanierBtn.setOnClickListener {
            if(cartMenuAL.isEmpty()) {
                Toast.makeText(requireContext(),"Votre panier est deja vide",Toast.LENGTH_SHORT).show()
            }
            else {
                showDeleteDialog()
            }
        }

        validerPanierBtn.setOnClickListener {
            val userAuth = checkUserAuth()
            if(!userAuth) {
                val intnt = Intent(requireContext(), LogIn::class.java)
                startActivity(intnt)
            }
            else {
                val sharedPrf = requireContext().getSharedPreferences("Auth", Context.MODE_PRIVATE)
                val editor = sharedPrf.edit()
                editor.putBoolean("auth",false)
                    .apply()
                Toast.makeText(requireContext(),"Validation de commande",Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun checkUserAuth():Boolean {
        val sharedPrf = requireContext().getSharedPreferences("Auth", Context.MODE_PRIVATE)
        val editor = sharedPrf.edit()
        var auth = sharedPrf.getBoolean("auth",false)
        return auth
    }

    fun updateUI() {

        val emptyImg = requireActivity().findViewById<ImageView>(R.id.emptyImg)
        val emptyText = requireActivity().findViewById<TextView>(R.id.emptyRV)
        if(cartMenuAL.isEmpty() || cartMenuAL.size==0) {
            cartRV.visibility = View.INVISIBLE
            emptyImg.visibility = View.VISIBLE
            emptyText.visibility = View.VISIBLE
        }
        else {
            cartRV.visibility = View.VISIBLE
            emptyImg.visibility = View.INVISIBLE
            emptyText.visibility = View.INVISIBLE
        }
    }

    fun getPrixTotal(cardList:List<CartMenu>):Double {
        var totalPrix:Double = 0.0
        for(cm in cardList) {
            totalPrix = totalPrix+cm.prix*cm.qte
        }
        return totalPrix
    }

    fun viderMonPanier() {
        GlobalScope.launch(Dispatchers.IO) {
            appDB.getMenuCardDao().deleteAllCart()
        }
    }

    fun emptyList( list:List<CartMenu>){
        for(cm in list) {
            list.drop(list.indexOf(cm)+1)
        }
    }


    fun showDeleteDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_remove_from_card)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val yesBtn = dialog.findViewById<Button>(R.id.removeFromCard)
        val cancelBut = dialog.findViewById<Button>(R.id.cancelRemove)

        yesBtn.setOnClickListener {

            viderMonPanier()
            cartMenuAL.clear()
            cartRV.adapter?.notifyDataSetChanged()
            updateUI()
            dialog.dismiss()
        }
        cancelBut.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
}