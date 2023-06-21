package com.example.fooddeliveryapp.Fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fooddeliveryapp.Adapters.MenuAdapter
import com.example.fooddeliveryapp.Auth.LogIn
import com.example.fooddeliveryapp.Entities.Restaurant
import com.example.fooddeliveryapp.R
import com.example.fooddeliveryapp.cnst.url
import com.example.fooddeliveryapp.viewmodel.MenuListViewModel
import com.example.fooddeliveryapp.viewmodel.UsersViewModel


class FragmentMenus : Fragment() {

    lateinit var menusVM:MenuListViewModel
    lateinit var userVM:UsersViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menus, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val resRating = requireActivity().findViewById<TextView>(R.id.ratingMenuRes)
        val resReviews = requireActivity().findViewById<TextView>(R.id.reviewMenuRes)
        val resName = requireActivity().findViewById<TextView>(R.id.nomResMenu)
        val resType = requireActivity().findViewById<TextView>(R.id.cuisineTypeResMenu)
        val menuRV = requireActivity().findViewById<RecyclerView>(R.id.menu_recycler)
        val resLogo = requireActivity().findViewById<ImageView>(R.id.imageView5)
        val rateRes = requireActivity().findViewById<Button>(R.id.rate_menu)



/***************************************   set up view model **********************************************************/
        menusVM = ViewModelProvider(requireActivity()).get(MenuListViewModel::class.java)
        userVM = ViewModelProvider(requireActivity()).get(UsersViewModel::class.java)


        //get restaurant object
        val restaurant:Restaurant = arguments?.getSerializable("restaurant") as Restaurant


        resName.setText(restaurant.name)//
        resType.setText(restaurant.foodTyp)
        Glide.with(requireContext()).load(url+restaurant.img_logo).into(resLogo)


        //menu recycler view set up
        val menuAdapter = MenuAdapter(requireContext(), findNavController(),menusVM)
        menuAdapter.setLifecycleOwner(viewLifecycleOwner)
        menuRV.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        menuRV.adapter = menuAdapter

        menusVM.getMenus(restaurant.id_rest).observe(viewLifecycleOwner) {//envoyer les donnée recupere vers l'adapter
            menuAdapter.submitData(it)
        }

        //rating dialog

        rateRes.setOnClickListener {
            if(!checkUserAuth()) {//user non athentifié => ouvrire la page de connexion
                val intnt = Intent(requireContext(), LogIn::class.java)
                startActivity(intnt)
            }
            else {//sinon afficher le dialogue pour inserer l'évaluation
                showRatingDialog(restaurant.id_rest)
            }
        }

        /**********************************   get restaurant rating and review       ***************************************/
        userVM.getRatings(restaurant.id_rest).observe(viewLifecycleOwner) {
            var rateAvg:Int = 0
            for(x in it) {
                rateAvg += x.rating
            }
            if(it.size!=0) {
                rateAvg /= it.size
            }else {
                rateAvg = 0
            }
            resRating.setText(rateAvg.toString())
            resReviews.setText(it.size.toString()+" évaluations")
        }

    }


    fun showRatingDialog(id_rest:Int) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_rating_res)


        val rateBar = dialog.findViewById<RatingBar>(R.id.ratingBar)
        val submitBut = dialog.findViewById<Button>(R.id.rate_but)

        submitBut.setOnClickListener {
            val rating = rateBar.rating.toInt()
            val uid = getUserId()
            userVM.addRating(uid,rating,id_rest)//inserer l'evaluation

            userVM._insertRatingStatus.observe(requireActivity()){
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
}