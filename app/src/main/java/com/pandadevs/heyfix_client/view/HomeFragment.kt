package com.pandadevs.heyfix_client.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.pandadevs.heyfix_client.adapters.CategoryAdapter
import com.pandadevs.heyfix_client.data.model.CategoryModel
import com.pandadevs.heyfix_client.databinding.FragmentHomeBinding
import com.pandadevs.heyfix_client.utils.SnackbarShow

class HomeFragment : Fragment(), CategoryAdapter.MyEvents {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var adapter: CategoryAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        /*   binding.btnRequestService.setOnClickListener {
               activity?.startActivity(Intent(context, RequestServiceActivity::class.java))
           }*/

        getAllCategories()

        return binding.root
    }

    private fun getAllCategories() {
        binding.llLoading.visibility = View.VISIBLE
        FirebaseFirestore.getInstance().collection("categories").get()
            .addOnSuccessListener {
                val categories: List<CategoryModel> = it.map { c ->
                    CategoryModel(
                        id = c.reference.id,
                        name = c.data["name"].toString(),
                        icon = c.data["icon"].toString(),
                        description = c.data["description"].toString()
                    )
                }
                setOnViewSuccess(categories)
            }
            .addOnFailureListener {
                setOnViewFailure()
            }
    }

    private fun setOnViewFailure() {
        binding.llLoading.visibility = View.INVISIBLE
        binding.tvNoCategories.visibility = View.VISIBLE
    }

    private fun setOnViewSuccess(list: List<CategoryModel>) {
        binding.llLoading.visibility = View.INVISIBLE
        if (list.isNotEmpty()) {
            binding.rvServices.layoutManager = GridLayoutManager(context, 2)
            adapter = CategoryAdapter(this, requireContext())
            binding.rvServices.adapter = adapter
            adapter?.submitList(list)
        } else binding.tvNoCategories.visibility = View.VISIBLE

    }

    override fun onClick(category: CategoryModel) {
        val intent = Intent(context, MapsActivity::class.java)
        intent.putExtra(MapsActivity.CATEGORY, category)
        activity?.startActivity(intent)
    }
}