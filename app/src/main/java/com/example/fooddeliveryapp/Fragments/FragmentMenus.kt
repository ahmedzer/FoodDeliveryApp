package com.example.fooddeliveryapp.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fooddeliveryapp.Adapters.MenuAdapter
import com.example.fooddeliveryapp.Entities.Menu
import com.example.fooddeliveryapp.Entities.Restaurant
import com.example.fooddeliveryapp.R
import com.example.fooddeliveryapp.Utils.prettyCount
import com.example.fooddeliveryapp.cnst.url


class FragmentMenus : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menus, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val resRating = requireActivity().findViewById<TextView>(R.id.ratingMenuRes)
        val resReviews = requireActivity().findViewById<TextView>(R.id.reviewMenuRes)
        val resName = requireActivity().findViewById<TextView>(R.id.nomResMenu)
        val resType = requireActivity().findViewById<TextView>(R.id.cuisineTypeResMenu)
        val menuRV = requireActivity().findViewById<RecyclerView>(R.id.menu_recycler)
        val resLogo = requireActivity().findViewById<ImageView>(R.id.imageView5)



        val restaurant:Restaurant = arguments?.getSerializable("restaurant") as Restaurant
        resRating.setText("5")
        resReviews.setText("00 avis sur ce restaurant")
        resName.setText(restaurant.name)
        resType.setText(restaurant.foodTyp)
        Glide.with(requireContext()).load(url+restaurant.img_logo)

        val menuList = ArrayList<Menu>()

        val menu1 = Menu(1,restaurant.id_rest,"Pizza Margherita","La pizza Margherita est une spécialité culinaire traditionnelle de la ville de Naples, en Italie. Très populaire, cette pizza napoletana est garnie de tomates, de mozzarella, de basilic frais, de sel et d'huile d'olive",
        1500.0,"Pizza",R.drawable.pizza,3.5,)
        val menu2 = Menu(2,restaurant.id_rest,"Pizza Reine","La pizza Margherita est une spécialité culinaire traditionnelle de la ville de Naples, en Italie. Très populaire, cette pizza napoletana est garnie de tomates, de mozzarella, de basilic frais, de sel et d'huile d'olive",
            2500.0,"Pizza",R.drawable.pizza2,4.5)
        val menu3 = Menu(3,restaurant.id_rest,"Spagetti Bolognese","La pizza Margherita est une spécialité culinaire traditionnelle de la ville de Naples, en Italie. Très populaire, cette pizza napoletana est garnie de tomates, de mozzarella, de basilic frais, de sel et d'huile d'olive",
            800.0,"Pizza",R.drawable.spagetti,3.5)
        val menu4 = Menu(4,restaurant.id_rest,"Pizza Margherita","La pizza Margherita est une spécialité culinaire traditionnelle de la ville de Naples, en Italie. Très populaire, cette pizza napoletana est garnie de tomates, de mozzarella, de basilic frais, de sel et d'huile d'olive",
            1500.0,"Pizza",R.drawable.pizza,3.5)
        val menu5 = Menu(5,restaurant.id_rest,"Pizza Margherita","La pizza Margherita est une spécialité culinaire traditionnelle de la ville de Naples, en Italie. Très populaire, cette pizza napoletana est garnie de tomates, de mozzarella, de basilic frais, de sel et d'huile d'olive",
            1500.0,"Pizza",R.drawable.pizza,3.5)
        menuList.add(menu1)
        menuList.add(menu2)
        menuList.add(menu3)
        menuList.add(menu4)
        menuList.add(menu5)

        val menuAdapter = MenuAdapter(requireContext(),menuList, findNavController())
        menuRV.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        menuRV.adapter = menuAdapter
    }


}