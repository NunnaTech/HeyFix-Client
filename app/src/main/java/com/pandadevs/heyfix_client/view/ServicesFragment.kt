package com.pandadevs.heyfix_client.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.pandadevs.heyfix_client.adapters.HistorialServiceAdapter
import com.pandadevs.heyfix_client.data.model.UserGet
import com.pandadevs.heyfix_client.databinding.FragmentServicesBinding
import com.pandadevs.heyfix_client.utils.SharedPreferenceManager
import com.pandadevs.heyfix_client.viewmodel.HistorialServicesViewModel
import kotlinx.coroutines.launch


class ServicesFragment : Fragment() {

    private var _binding: FragmentServicesBinding? = null
    private val binding get() = _binding!!
    private val historialServicesViewModel: HistorialServicesViewModel by activityViewModels()
    var adapter: HistorialServiceAdapter? = null;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentServicesBinding.inflate(inflater, container, false)

        initView()
        initObservers()
        return binding.root
    }

    private fun initObservers() {
        historialServicesViewModel.historialServices.observe(viewLifecycleOwner) {
            if(it.isNotEmpty()) {
                binding.rvHistorialServices.layoutManager = LinearLayoutManager(requireActivity())
                adapter = HistorialServiceAdapter(requireActivity())
                binding.rvHistorialServices.adapter = adapter
                adapter!!.submitList(it)
                adapter!!.notifyDataSetChanged()
                binding.tvNoHistorialServices.visibility = View.GONE
            } else {
                binding.tvNoHistorialServices.visibility = View.VISIBLE
                binding.rvHistorialServices.visibility = View.GONE
            }
        }
    }

    private fun initView() {
        val user: UserGet = SharedPreferenceManager(this.requireContext()).getUser()!!
        lifecycleScope.launch { historialServicesViewModel.getHistorialServices(user.id) }
    }


}