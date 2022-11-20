package com.pandadevs.heyfix_client.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.pandadevs.heyfix_client.adapters.CategoryAdapter
import com.pandadevs.heyfix_client.data.model.CategoryModel
import com.pandadevs.heyfix_client.data.model.UserGet
import com.pandadevs.heyfix_client.databinding.FragmentHomeBinding
import com.pandadevs.heyfix_client.utils.SharedPreferenceManager
import com.pandadevs.heyfix_client.utils.TimeDay
import com.pandadevs.heyfix_client.viewmodel.CategoryViewModel
import kotlinx.coroutines.launch

class HomeFragment : Fragment(), CategoryAdapter.MyEvents {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var adapter: CategoryAdapter? = null
    private lateinit var viewModel: CategoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[CategoryViewModel::class.java]
        initView()
        initObservers()
        return binding.root
    }

    private fun initView() {
        val user:UserGet? = SharedPreferenceManager(this.requireContext()).getUser()
        binding.tvName.text = user?.name
        binding.tvWelcome.text = TimeDay.getTime()
        lifecycleScope.launch{ viewModel.getAllCategories() }
    }

    private fun initObservers() {
        viewModel.isLoading.observe(viewLifecycleOwner){
            binding.llLoading.visibility = if (it) View.VISIBLE else View.INVISIBLE
        }
        viewModel.error.observe(viewLifecycleOwner){
            binding.tvNoCategories.visibility = if (it) View.VISIBLE else View.INVISIBLE
        }
        viewModel.result.observe(viewLifecycleOwner){
            binding.rvServices.layoutManager = GridLayoutManager(context, 2)
            adapter = CategoryAdapter(this, requireContext())
            binding.rvServices.adapter = adapter
            adapter?.submitList(it)
        }
    }

    override fun onClick(category: CategoryModel) {
        val intent = Intent(context, MapsActivity::class.java)
        intent.putExtra(MapsActivity.CATEGORY, category)
        activity?.startActivity(intent)
    }
}