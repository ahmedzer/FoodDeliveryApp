package com.example.fooddeliveryapp.Fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fooddeliveryapp.Adapters.CartAdapter
import com.example.fooddeliveryapp.Auth.LogIn
import com.example.fooddeliveryapp.DB.Dao.AppDataBase
import com.example.fooddeliveryapp.DB.Dao.CartMenu
import com.example.fooddeliveryapp.Entities.Commande
import com.example.fooddeliveryapp.R
import com.example.fooddeliveryapp.viewmodel.UsersViewModel
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*


@OptIn(DelicateCoroutinesApi::class)
class FragmentPanier : Fragment() {
    private lateinit var appDB: AppDataBase
    private lateinit var cartRV:RecyclerView
    private lateinit var CartMenus:List<CartMenu>
    private lateinit var cartMenuAL:ArrayList<CartMenu>
    private lateinit var userVM:UsersViewModel


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

        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        cartRV = requireActivity().findViewById<RecyclerView>(R.id.cartRV)
        val viderPanierBtn = requireActivity().findViewById<Button>(R.id.viderPanier)
        val validerPanierBtn = requireActivity().findViewById<Button>(R.id.validerPanier)
        val totalPrixText = requireActivity().findViewById<TextView>(R.id.prixTotal)

        userVM = ViewModelProvider(requireActivity()).get(UsersViewModel::class.java)


        CartMenus = appDB.getMenuCardDao().getAllCartMenus()

        val totalPrix:Double = getPrixTotal(CartMenus)
        totalPrixText.setText("Prix total : "+totalPrix.toString()+" da")

        cartMenuAL = ArrayList(CartMenus)
        cartRV.adapter = CartAdapter(requireContext(),cartMenuAL)
        cartRV.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        updateUI()
        var menus_str =""
        GlobalScope.launch(Dispatchers.IO) {

            for (cm in cartMenuAL) {
                menus_str = menus_str+cm.nom_menu+" X "+cm.qte+" ,"
            }

        }

        viderPanierBtn.setOnClickListener {
            if(cartMenuAL.isEmpty()) {
                Toast.makeText(requireContext(),"Votre panier est deja vide",Toast.LENGTH_SHORT).show()
            }
            else {
                showDeleteDialog()
            }
        }
        val userAuth = checkUserAuth()
        validerPanierBtn.setOnClickListener {

            if(!userAuth) {
                val intnt = Intent(requireContext(), LogIn::class.java)
                startActivity(intnt)
            }
            else {
                //get menu list to string
                if(cartMenuAL.isEmpty()) {
                    Toast.makeText(requireContext(),"Votre panier est vide",Toast.LENGTH_SHORT).show()
                }
                else {
                        showCommandDialog(menus_str,totalPrix)
                }
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
            cartRV.visibility = View.GONE
            emptyImg.visibility = View.VISIBLE
            emptyText.visibility = View.VISIBLE
        }
        else {
            cartRV.visibility = View.VISIBLE
            emptyImg.visibility = View.GONE
            emptyText.visibility = View.GONE
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



    fun showCommandDialog(menus:String, totalPrix:Double) {

        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)

        dialog.setContentView(R.layout.dialog_location)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)



        val adrText = dialog.findViewById<EditText>(R.id.adrLiv)
        val confBtn = dialog.findViewById<Button>(R.id.confCom)



        dialog.show()

        confBtn.setOnClickListener {
            val commande=Commande(0,getUserId(),"En attente",adrText.text.toString(),
                menus,getCurrentDateAsString(),totalPrix,"na")

            userVM.insertCommand(commande)

            userVM._insertCmdStatus.observe(requireActivity()){
                if(it.isSuccess) {
                    Toast.makeText(requireContext(),"Commande validée",Toast.LENGTH_SHORT).show()
                }
                else {
                    Toast.makeText(requireContext(),"Erreur dans la validation de la commande, verifiez votre connexion INTERNET",Toast.LENGTH_SHORT).show()
                    Log.d("add commande failure",it.exceptionOrNull()!!.message.toString())
                }
            }
            updateUI()
            dialog.dismiss()
        }

    }

    fun getUserId():Int {//recuperer le user_id sauvegardé
        val sharedPrf = requireContext().getSharedPreferences("Auth", Context.MODE_PRIVATE)
        val uid  = sharedPrf.getInt("userId",-1)
        return uid
    }

    @SuppressLint("SimpleDateFormat")
    fun getCurrentDateAsString(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        return dateFormat.format(calendar.time)
    }



}